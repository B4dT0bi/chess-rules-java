package org.alcibiade.chess.integration;

import org.alcibiade.chess.engine.ChessEngineController;
import org.alcibiade.chess.engine.ChessEngineFailureException;
import org.alcibiade.chess.model.*;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.alcibiade.chess.rules.ChessRules;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class ChessEnginesTest {

    public static final int FULL_GAMES = 10;
    private Logger log = LoggerFactory.getLogger(ChessEnginesTest.class);
    @Autowired
    private List<ChessEngineController> engines;
    @Autowired
    private ChessRules chessRules;
    @Autowired
    private PgnMarshaller pgnMarshaller;
    private Random random = new Random();

    @Test
    public void testInitialMove() throws ChessEngineFailureException {
        for (ChessEngineController engine : engines) {
            log.debug(engine.computeNextMove(2, 0, new ArrayList<String>()));
        }
    }

    @Test
    public void testFullGames() throws ChessException {
        for (ChessEngineController engine : engines) {
            int success = 0;
            int iterations = 0;

            while (iterations < FULL_GAMES) {
                iterations += 1;
                try {
                    testFullGame(engine);
                    success += 1;
                } catch (ChessException e) {
                    log.debug(e.getLocalizedMessage(), e);
                    throw e;
                }
            }

            log.debug("  Games played: " + iterations);
            log.debug("     Successes: " + success);

            Assertions.assertThat(success).isEqualTo(iterations);
        }
    }

    public void testFullGame(ChessEngineController engine) throws ChessEngineFailureException, PgnMoveException,
            IllegalMoveException {
        ChessSide randomSide = random.nextBoolean() ? ChessSide.WHITE : ChessSide.BLACK;

        log.debug("Starting RvR game for {}", engine);
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
                move = engine.computeNextMove(5, 0, moves);
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
}
