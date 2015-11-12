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

        for (int i = 0; i < 5; i++) {
            PgnGameModel gameModel;
            InputStream stream = this.getClass().getResourceAsStream("McDonnell.pgn");
            PgnBookReader bookReader = new PgnBookReader(stream);

            while ((gameModel = bookReader.readGame()) != null) {
                logger.debug("Parsing game {}", gameModel);

                ChessPosition position = rules.getInitialPosition();

                for (String movePgn : gameModel.getMoves()) {
                    ChessMovePath move = pgnMarshaller.convertPgnToMove(position, movePgn);
                    position = ChessHelper.applyMoveAndSwitch(rules, position, move);
                }
            }
        }

        long te = System.currentTimeMillis();

        logger.debug(String.format("Duration: %.3f", (te - ts) / 1000.));
    }
}
