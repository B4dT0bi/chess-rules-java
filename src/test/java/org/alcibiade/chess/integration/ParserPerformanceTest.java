package org.alcibiade.chess.integration;

import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.persistence.PgnBookReader;
import org.alcibiade.chess.persistence.PgnGameModel;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.alcibiade.chess.persistence.PgnMarshallerImpl;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.ChessRulesImpl;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;

/**
 * Parser benchmark.
 *
 * @author Yannick Kirschhoffer alcibiade@alcibiade.org
 */
public class ParserPerformanceTest {

    private Logger logger = LoggerFactory.getLogger(ParserPerformanceTest.class);

    public static void main(String... args) throws IOException {
        new ParserPerformanceTest().testParsingPerformance();
    }

    @Test
    public void testParsingPerformance() throws IOException {

        ChessRules rules = new ChessRulesImpl();
        PgnMarshaller pgnMarshaller = new PgnMarshallerImpl(rules);

        long ts = System.currentTimeMillis();

        // Duration depending on Collection used in PieceMoveManager for 10 iterations:
        // Reference duration with release 1.3.1: 48.9s
        //
        // Reachable: HashSet - isOpponentOrFree accumulator:
        //   LinkedList - 16.1s
        //   ArrayList  - 15.3s
        //   TreeSet    - 17.2s
        //   HashSet    - 21.6s
        // Reachable: TreeSet - isOpponentOrFree accumulator: ArrayList - 14.3s !
        //
        // Updated the piece locator to iterate over arrays: 12.8s

        for (int i = 0; i < 10; i++) {
            PgnGameModel gameModel;
            InputStream stream = this.getClass().getResourceAsStream("McDonnell.pgn");
            PgnBookReader bookReader = new PgnBookReader(stream);

            while ((gameModel = bookReader.readGame()) != null) {
                logger.trace("Parsing game {}", gameModel);

                ChessPosition position = rules.getInitialPosition();

                for (String movePgn : gameModel.getMoves()) {
                    ChessMovePath move = pgnMarshaller.convertPgnToMove(position, movePgn);
                    position = ChessHelper.applyMoveAndSwitch(rules, position, move);
                }
            }
        }

        long te = System.currentTimeMillis();

        logger.debug(String.format("Duration: %.3fs", (te - ts) / 1000.));
    }
}
