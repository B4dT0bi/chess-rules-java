package org.alcibiade.chess.integration;

import java.io.IOException;
import java.util.zip.GZIPInputStream;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.PgnMoveException;
import org.alcibiade.chess.persistence.PgnBookReader;
import org.alcibiade.chess.persistence.PgnGameModel;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class GnuChessBookTest {

    private Logger log = LoggerFactory.getLogger(GnuChessBookTest.class);
    @Autowired
    private ChessRules chessRules;
    @Autowired
    private PgnMarshaller pgnMarshaller;
    @Value("classpath:/book_1.02.pgn.gz")
    private Resource pgnBook;

    @Test
    public void testBookGames() throws IOException, PgnMoveException, IllegalMoveException {
        log.debug("Book length is {} kbytes", pgnBook.contentLength() / 1024);
        try (PgnBookReader bookReader = new PgnBookReader(new GZIPInputStream(pgnBook.getInputStream()))) {
            int gameIndex = 0;

            while (true) {
                gameIndex += 1;
                PgnGameModel game = bookReader.readGame();

                if (game == null) {
                    break;
                }

                log.debug("Game {}: {}", String.format("%06d", gameIndex), game);

                ChessPosition position = chessRules.getInitialPosition();

                for (String pgnMove : game.getMoves()) {
                    log.trace("   - " + pgnMove);
                    ChessMovePath move = pgnMarshaller.convertPgnToMove(position, pgnMove);
                    position = ChessHelper.applyMoveAndSwitch(chessRules, position, move);
                }
            }
        }
    }
}
