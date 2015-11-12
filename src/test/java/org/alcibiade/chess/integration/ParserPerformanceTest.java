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

    @Test
    public void testParsingPerformance() throws IOException {
        InputStream stream = this.getClass().getResourceAsStream("McDonnell.pgn");
        PgnBookReader bookReader = new PgnBookReader(stream);

        ChessRules rules = new ChessRulesImpl();
        PgnMarshaller pgnMarshaller = new PgnMarshallerImpl(rules);

        PgnGameModel gameModel;

        while ((gameModel = bookReader.readGame()) != null) {
            logger.debug("Parsing game {}", gameModel);

            ChessPosition position = rules.getInitialPosition();

            for (String movePgn : gameModel.getMoves()) {
                ChessMovePath move = pgnMarshaller.convertPgnToMove(position, movePgn);
                position = ChessHelper.applyMoveAndSwitch(rules, position, move);
            }
        }
    }
}
