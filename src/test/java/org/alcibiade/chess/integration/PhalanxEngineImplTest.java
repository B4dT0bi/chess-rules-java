package org.alcibiade.chess.integration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import org.alcibiade.chess.engine.ChessEngineController;
import org.alcibiade.chess.engine.ChessEngineFailureException;
import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessException;
import org.alcibiade.chess.model.ChessGameStatus;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessSide;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.PgnMoveException;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.PgnMarshaller;
import org.junit.Assert;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class PhalanxEngineImplTest {

    public static final int FULL_GAMES = 100;
    private Logger log = LoggerFactory.getLogger(PhalanxEngineImplTest.class);
    @Autowired
    @Qualifier("phalanx")
    private ChessEngineController phalanx;
    @Autowired
    private ChessRules chessRules;
    @Autowired
    private PgnMarshaller pgnMarshaller;
    private Random random = new Random();

    @Test
    public void testInitialMove() throws ChessEngineFailureException {
        log.debug(phalanx.computeNextMove(2, 0, new ArrayList<String>()));
    }

    @Test
    public void testFullGames() throws ChessException {

        int success = 0;
        int iterations = 0;

        while (iterations < FULL_GAMES) {
            iterations += 1;
            try {
                testFullGame();
                success += 1;
            } catch (ChessException e) {
                log.debug(e.getLocalizedMessage(), e);
                throw e;
            }
        }

        log.debug("  Games played: " + iterations);
        log.debug("     Successes: " + success);

        assertEquals(iterations, success);
    }

    public void testFullGame() throws ChessEngineFailureException, PgnMoveException,
            IllegalMoveException {
        ChessSide randomSide = random.nextBoolean() ? ChessSide.WHITE : ChessSide.BLACK;

        log.debug("Starting phalanx vs. phalanx game");
        List<String> moves = new ArrayList<>();

        ChessBoardModel board = new ChessBoardModel();
        board.setPosition(chessRules.getInitialPosition());

        while (chessRules.getStatus(board) == ChessGameStatus.OPEN) {
            log.debug("-----------------------------------------");
            log.debug("Position: " + board);
            log.debug("Available moves: " + chessRules.getAvailableMoves(board));

            String move;

            if (board.getNextPlayerTurn() == randomSide) {
                Collection<ChessMovePath> availableMoves = chessRules.getAvailableMoves(board);
                ChessMovePath movePath = pickRandom(availableMoves);
                move = pgnMarshaller.convertMoveToPgn(board, movePath);
            } else {
                move = phalanx.computeNextMove(5, 0, moves);
            }

            log.debug("" + board.getNextPlayerTurn() + " move: " + move);

            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(board, move);
            List<ChessBoardUpdate> updates = chessRules.getUpdatesForMove(board, movePath);

            for (ChessBoardUpdate update : updates) {
                update.apply(board);
            }

            board.nextPlayerTurn();

            moves.add(move);
        }

        log.debug("Game duration : " + moves.size() + " half moves");
        log.debug("Game issue    : " + chessRules.getStatus(board));
    }

    private ChessMovePath pickRandom(Collection<ChessMovePath> availableMoves) {
        int size = availableMoves.size();
        int selected = random.nextInt(size);

        Iterator<ChessMovePath> availableMovesIt = availableMoves.iterator();

        for (int i = 0; i < selected; i++) {
            assert availableMovesIt.hasNext();
            availableMovesIt.next();
        }

        return availableMovesIt.next();
    }

    @Test
    public void testBrokenMove() {
        try {
            phalanx.computeNextMove(5, 0, Arrays.asList("e4", "d5", "dumbo"));
            Assert.fail();
        } catch (ChessEngineFailureException e) {
            Assert.assertTrue(e.getCause().getLocalizedMessage().contains("dumbo"));
        }
    }
}
