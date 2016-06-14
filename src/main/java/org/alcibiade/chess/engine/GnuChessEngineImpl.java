package org.alcibiade.chess.engine;

import org.alcibiade.chess.engine.process.ExternalProcess;
import org.alcibiade.chess.engine.process.ExternalProcessFactory;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Qualifier("gnuchess")
public class GnuChessEngineImpl implements ChessEngineAnalyticalController {

    private static final String MYMOVE_PATTERN = "My move is : (.*)";
    private Logger log = LoggerFactory.getLogger(GnuChessEngineImpl.class);
    @Value("${gnuchess.command:gnuchess}")
    private String gnuchessCommand;
    @Autowired
    private ExternalProcessFactory externalProcessFactory;

    @PostConstruct
    public void validateCompatibility() throws IOException {
        try (ExternalProcess process = externalProcessFactory.run(gnuchessCommand, "--version")) {
            String version = process.read(Pattern.compile("(.*)"));

            if (StringUtils.startsWith(version, "GNU Chess 5.") || StringUtils.startsWith(version, "GNU Chess 6.")) {
                log.info("Detected GnuChess engine: " + version);
            } else {
                throw new IllegalStateException("Provided gnuchess not supported: " + version);
            }
        }
    }

    @Override
    public String computeNextMove(int depth, int random, Collection<String> game) throws ChessEngineFailureException {
        if (random > 0) {
            throw new IllegalStateException("Randomization not supported in GnuChess");
        }

        String inputScript = createInputScript(game, depth);
        Pattern nextMovePattern = Pattern.compile(MYMOVE_PATTERN);
        try (ExternalProcess externalProcess = externalProcessFactory.run(gnuchessCommand)) {
            externalProcess.write(inputScript);
            String nextMove = externalProcess.read(nextMovePattern);
            externalProcess.write("exit\n");
            return nextMove;
        } catch (IOException ex) {
            throw new ChessEngineFailureException(ex);
        }
    }

    @Override
    public EngineAnalysisReport analyze(Collection<String> moves) throws ChessEngineFailureException {
        Pattern resultPattern = Pattern.compile("^12 (.*?) (.*?) (.*?) (.*)");
        String inputScript = createAnalysisScript(moves, 12);

        try (ExternalProcess externalProcess = externalProcessFactory.run(gnuchessCommand)) {
            externalProcess.write(inputScript);
            Matcher matcher = externalProcess.readForMatcher(resultPattern);
            externalProcess.write("exit\n");
            int score = Integer.parseInt(matcher.group(1));
            String variant = matcher.group(4);
            String[] variantMoves = StringUtils.split(variant);
            List<String> variantList = Arrays.asList(variantMoves);
            return new EngineAnalysisReport(score, variantList);
        } catch (IOException ex) {
            throw new ChessEngineFailureException(ex);
        }
    }

    private String createInputScript(Collection<String> moves, int depth) {
        StringBuilder script = new StringBuilder();

        script.append("easy\n");
        script.append("force\n");
        script.append("depth ");
        script.append(depth);
        script.append("\n");

        for (String move : moves) {
            script.append(move);
            script.append("\n");
        }

        script.append("go\n");

        return script.toString();
    }

    private String createAnalysisScript(Collection<String> moves, int depth) {
        StringBuilder script = new StringBuilder();

        script.append("easy\n");
        script.append("force\n");
        script.append("post\n");
        script.append("depth ");
        script.append(depth);
        script.append("\n");

        for (String move : moves) {
            script.append(move);
            script.append("\n");
        }

        script.append("go\n");

        return script.toString();
    }
}
