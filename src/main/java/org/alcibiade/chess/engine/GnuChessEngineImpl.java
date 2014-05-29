package org.alcibiade.chess.engine;

import javax.annotation.PostConstruct;
import org.alcibiade.chess.engine.process.ExternalProcess;
import org.alcibiade.chess.engine.process.ExternalProcessFactory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Qualifier("gnuchess")
public class GnuChessEngineImpl implements ChessEngineController {

    private static final String MYMOVE_PATTERN = "My move is : (.*)";
    private Logger log = LoggerFactory.getLogger(GnuChessEngineImpl.class);
    @Value("${gnuchess.command:gnuchess}")
    private String gnuchessCommand;
    private int gnuChessVersion;
    @Autowired
    private ExternalProcessFactory externalProcessFactory;

    @PostConstruct
    public void validateCompatibility() throws IOException {
        try (ExternalProcess process = externalProcessFactory.run(gnuchessCommand, "--version")) {
            String version = process.read(Pattern.compile("(.*)"));

            if (StringUtils.startsWith(version, "GNU Chess 5.")) {
                gnuChessVersion = 5;
            } else if (StringUtils.startsWith(version, "GNU Chess 6.")) {
                gnuChessVersion = 6;
            } else {
                throw new IllegalStateException("Provided gnuchess not supported: " + version);
            }

            log.info("Detected GnuChess engine: " + version);
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

    private String createInputScript(Collection<String> moves, int depth) {
        StringBuilder script = new StringBuilder();

        script.append("easy\n");
        script.append("force\n");
        script.append("depth " + depth + "\n");

        for (String move : moves) {
            script.append(move);
            script.append("\n");
        }

        script.append("go\n");

        return script.toString();
    }
}
