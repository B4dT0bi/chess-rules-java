package org.alcibiade.chess.engine;

import org.alcibiade.chess.engine.process.ExternalProcess;
import org.alcibiade.chess.engine.process.ExternalProcessFactory;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

@Component
@Qualifier("gnuchess")
public class GnuChessEngineImpl implements ChessEngineAnalyticalController {

    public static final int MATE_SCORE = -10_000;
    public static final Pattern MYMOVE_PATTERN = Pattern.compile("My move is : (.*)");
    public static final Pattern ANALYSIS_RESULT_PATTERN = Pattern.compile("^ *\\d+\\.?\\s+([+\\-0-9\\.]+?)\\s+(-?\\d+)\\s+(\\d+)\\s+(.*)");
    private Logger logger = LoggerFactory.getLogger(GnuChessEngineImpl.class);
    private int majorVersion;
    @Value("${gnuchess.command:gnuchess}")
    private String gnuchessCommand;
    @Value("${gnuchess.analysis.depth:8}")
    private int analysisDepth;
    @Autowired
    private ExternalProcessFactory externalProcessFactory;
    @Autowired
    private ChessRules chessRules;
    @Autowired
    private PgnMarshaller pgnMarshaller;

    @PostConstruct
    public void validateCompatibility() throws IOException {
        try (ExternalProcess process = externalProcessFactory.run(gnuchessCommand, "--version")) {
            String version = process.read(Pattern.compile("(.*)"));

            if (StringUtils.startsWith(version, "GNU Chess 5.")) {
                majorVersion = 5;
                logger.info("Detected GnuChess engine: " + version);
            } else if (StringUtils.startsWith(version, "GNU Chess 6.")) {
                majorVersion = 6;
                logger.info("Detected GnuChess engine: " + version);
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
        try (ExternalProcess externalProcess = externalProcessFactory.run(gnuchessCommand)) {
            externalProcess.write(inputScript);
            String nextMove = externalProcess.read(MYMOVE_PATTERN);
            externalProcess.write("exit\n");
            return nextMove;
        } catch (IOException ex) {
            throw new ChessEngineFailureException(ex);
        }
    }

    @Override
    public EngineAnalysisReport analyze(Collection<String> moves) throws ChessEngineFailureException {
        String inputScript = createAnalysisScript(moves, this.analysisDepth);

        // If checkmate, return the MATE score directly
        ChessPosition position = ChessHelper.movesToPosition(chessRules, pgnMarshaller, moves);
        Set<ChessMovePath> availableMoves = chessRules.getAvailableMoves(position);
        logger.debug("Available moves for analysis are {}", availableMoves);
        if (availableMoves.isEmpty()) {
            return new EngineAnalysisReport(MATE_SCORE, new ArrayList<String>());
        }

        try (ExternalProcess externalProcess = externalProcessFactory.run(gnuchessCommand)) {
            externalProcess.write(inputScript);
            String[] values = externalProcess.readForArray(ANALYSIS_RESULT_PATTERN, MYMOVE_PATTERN);
            externalProcess.write("exit\n");
            int score = Integer.parseInt(majorVersion == 5 ? values[1] : values[0]);
            String variant = values[3];
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
        script.append("book off\n");
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

    @Override
    public String toString() {
        return "GnuChessEngineImpl{" +
                "gnuchessCommand='" + gnuchessCommand + '\'' +
                ", majorVersion=" + majorVersion +
                '}';
    }
}
