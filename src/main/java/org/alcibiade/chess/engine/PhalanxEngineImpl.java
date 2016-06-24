package org.alcibiade.chess.engine;

import org.alcibiade.chess.engine.process.ExternalProcess;
import org.alcibiade.chess.engine.process.ExternalProcessFactory;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Collection;
import java.util.regex.Pattern;

@Component
@Qualifier("phalanx")
public class PhalanxEngineImpl implements ChessEngineController {

    private static final String MYMOVE_PATTERN = "my move is (.*)";
    private static final String ERROR_PATTERN = "Illegal move: (.*)";

    private Logger log = LoggerFactory.getLogger(PhalanxEngineImpl.class);
    @Value("${phalanx.command:phalanx}")
    private String phalanxCommand;
    @Autowired
    private ExternalProcessFactory externalProcessFactory;
    @Autowired
    private PgnMarshaller pgnMarshaller;

    @PostConstruct
    public void validateCompatibility() throws IOException {
        try (ExternalProcess process = externalProcessFactory.run(phalanxCommand, "--version")) {
            String version = process.read(Pattern.compile("(.*)"));

            if (StringUtils.endsWith(version, "XXII") || StringUtils.endsWith(version, "XXII-pg")) {
                log.info("Detected Phalanx Chess engine: " + version);
            } else {
                throw new IllegalStateException("Provided phalanx not supported: " + version);
            }
        }
    }

    @Override
    public String computeNextMove(int depth, int random, Collection<String> game) throws ChessEngineFailureException {
        String inputScript = createInputScript(game, depth);
        Pattern nextMovePattern = Pattern.compile(MYMOVE_PATTERN);
        Pattern errorPattern = Pattern.compile(ERROR_PATTERN);
        try (ExternalProcess externalProcess = externalProcessFactory.run(phalanxCommand, "-e" + random, "-l-")) {
            externalProcess.write(inputScript);
            String nextMove = externalProcess.read(nextMovePattern, errorPattern);
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
        return "PhalanxEngineImpl{" +
                "phalanxCommand='" + phalanxCommand + '\'' +
                '}';
    }
}
