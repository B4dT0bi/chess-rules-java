package org.alcibiade.chess.engine;

import org.alcibiade.chess.engine.process.ExternalProcess;
import org.alcibiade.chess.engine.process.ExternalProcessFactory;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.PgnMoveException;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.alcibiade.chess.persistence.PositionMarshaller;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
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
@Qualifier("crafty")
public class CraftyEngineImpl implements ChessEngineController {

    private static final String MYMOVE_PATTERN = ".*\\): (.*)";

    private Logger log = LoggerFactory.getLogger(CraftyEngineImpl.class);
    @Value("${crafty.command:crafty}")
    private String craftyCommand;
    @Autowired
    @Qualifier("fen")
    private PositionMarshaller fenMarshaller;
    @Autowired
    private ChessRules chessRules;
    @Autowired
    private ExternalProcessFactory externalProcessFactory;
    @Autowired
    private PgnMarshaller pgnMarshaller;

    @PostConstruct
    public void validateCompatibility() throws IOException {
        try (ExternalProcess process = externalProcessFactory.run(craftyCommand, "log off", "ponder off")) {
            String version = process.read(Pattern.compile("Crafty v(.*?) .*", Pattern.CASE_INSENSITIVE));
            process.write("exit\n");
            log.info("Detected crafty Chess engine: " + version);
        }
    }

    @Override
    public String computeNextMove(int depth, int random, Collection<String> game) throws ChessEngineFailureException {
        Pattern nextMovePattern = Pattern.compile(MYMOVE_PATTERN);
        try (ExternalProcess externalProcess = externalProcessFactory.run(craftyCommand, "log off", "ponder off", "sd " + depth)) {
            ChessPosition position = ChessHelper.movesToPosition(chessRules, pgnMarshaller, game);
            externalProcess.write("setboard " + fenMarshaller.convertPositionToString(position) + "\n");
            externalProcess.write("go\n");
            // Skip first prompt related to the setboard command
            externalProcess.read(nextMovePattern);
            String nextMove = externalProcess.read(nextMovePattern);
            externalProcess.write("exit\n");
            return nextMove;
        } catch (IOException | PgnMoveException ex) {
            throw new ChessEngineFailureException(ex);
        }
    }

    private String createInputScript(Collection<String> moves, int depth) {
        ChessPosition position = ChessHelper.movesToPosition(chessRules, pgnMarshaller, moves);

        StringBuilder script = new StringBuilder();

        script.append("setboard ").append(fenMarshaller.convertPositionToString(position)).append("\n");

        script.append("go\n");

        return script.toString();
    }
}
