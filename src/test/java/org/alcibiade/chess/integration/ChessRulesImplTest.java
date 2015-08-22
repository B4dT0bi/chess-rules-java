package org.alcibiade.chess.integration;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.rules.Castling;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Set;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class ChessRulesImplTest {

    private Logger log = LoggerFactory.getLogger(ChessRulesImplTest.class);
    @Autowired
    private ChessRules chessRules;

    @Test
    public void testInitialPosition() {
        ChessPosition position = chessRules.getInitialPosition();

        assertEquals(new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE),
                position.getPiece(new ChessBoardCoord(5, 1)));

        assertEquals(new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK),
                position.getPiece(new ChessBoardCoord(2, 6)));

        assertEquals(true, position.isCastlingAvailable(ChessSide.WHITE, true));
        assertEquals(true, position.isCastlingAvailable(ChessSide.BLACK, true));
        assertEquals(true, position.isCastlingAvailable(ChessSide.WHITE, false));
        assertEquals(true, position.isCastlingAvailable(ChessSide.BLACK, false));
        assertEquals(ChessSide.WHITE, position.getNextPlayerTurn());
        assertEquals(null, position.getLastPawnDMove());
    }

    @Test
    public void testAvailableMoves() {
        ChessPosition position = chessRules.getInitialPosition();
        ChessBoardCoord e4 = new ChessBoardCoord("e4");
        ChessBoardCoord e2 = new ChessBoardCoord("e2");
        ChessMovePath e2e4 = new ChessMovePath(e2, e4);

        Set<ChessMovePath> availableMoves = chessRules.getAvailableMoves(position);

        assertEquals(20, availableMoves.size());
        assertTrue(availableMoves.contains(e2e4));
    }

    @Test
    public void testGameStatus() {
        ChessPosition position = chessRules.getInitialPosition();
        assertEquals(ChessGameStatus.OPEN, chessRules.getStatus(position));
    }

    @Test
    public void testCanKingTakeAProtectedPiece() {
        ChessBoardModel board = new ChessBoardModel();
        board.setPiece(new ChessBoardCoord("e1"), new ChessPiece(ChessPieceType.KING, ChessSide.WHITE));
        board.setPiece(new ChessBoardCoord("e2"), new ChessPiece(ChessPieceType.QUEEN, ChessSide.BLACK));
        board.setPiece(new ChessBoardCoord("f3"), new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK));

        log.debug("Can the king take this protected queen ?\n" + board);
        Set<ChessMovePath> available = chessRules.getAvailableMoves(board);

        log.debug("Available moves: " + available);
        assertFalse(available.contains(new ChessMovePath("e1", "e2")));
    }

    @Test
    public void testLosingRookPreventsCastling() throws IllegalMoveException {
        ChessBoardModel position = new ChessBoardModel();
        position.setPosition(chessRules.getInitialPosition());

        ChessMovePath[] paths = {new ChessMovePath("g2", "g3"), new ChessMovePath("b7", "b6"),
            new ChessMovePath("f2", "f4"), new ChessMovePath("c8", "b7"), new ChessMovePath("e2", "e3"),
            new ChessMovePath("b7", "h1")};

        for (ChessMovePath path : paths) {
            position = ChessHelper.applyMove(chessRules, position, path);
            position.nextPlayerTurn();
        }

        assertFalse(position.isCastlingAvailable(ChessSide.WHITE, true));
        assertTrue(position.isCastlingAvailable(ChessSide.WHITE, false));
        assertTrue(position.isCastlingAvailable(ChessSide.BLACK, true));
        assertTrue(position.isCastlingAvailable(ChessSide.BLACK, false));
    }

    @Test
    public void testPromotion() throws IllegalMoveException {
        ChessBoardModel position = new ChessBoardModel();
        position.setPiece(new ChessBoardCoord("e7"), new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE));

        position = ChessHelper.applyMove(chessRules, position, new ChessMovePath("e7", "e8"));
        assertEquals(ChessPieceType.QUEEN, position.getPiece(new ChessBoardCoord("e8")).getType());
    }

    @Test
    public void testPromotionKnight() throws IllegalMoveException {
        ChessBoardModel position = new ChessBoardModel();
        position.setPiece(new ChessBoardCoord("e7"), new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE));

        position = ChessHelper.applyMove(chessRules, position, new ChessMovePath("e7", "e8", ChessPieceType.KNIGHT));
        assertEquals(ChessPieceType.KNIGHT, position.getPiece(new ChessBoardCoord("e8")).getType());
    }

    @Test
    public void testEnPassant() throws IllegalMoveException {
        ChessBoardModel position = new ChessBoardModel();
        position.setPosition(chessRules.getInitialPosition());

        ChessMovePath[] paths = {new ChessMovePath("e2", "e4"), new ChessMovePath("a7", "a6"),
            new ChessMovePath("e4", "e5"), new ChessMovePath("d7", "d5")};

        for (ChessMovePath path : paths) {
            position = ChessHelper.applyMove(chessRules, position, path);
            position.nextPlayerTurn();
        }

        Set<ChessMovePath> availableMoves = chessRules.getAvailableMoves(position);

        assertEquals(new ChessBoardCoord("d5"), position.getLastPawnDMove());
        assertFalse(availableMoves.contains(new ChessMovePath("e5", "f6")));
        assertTrue(availableMoves.contains(new ChessMovePath("e5", "d6")));
        log.debug(position.toString());
        position = ChessHelper.applyMove(chessRules, position, new ChessMovePath("e5", "d6"));
        position.nextPlayerTurn();
        log.debug(position.toString());

        assertNull(position.getPiece(new ChessBoardCoord("d5")));
    }

    @Test
    public void testCastlingBlocked() throws IllegalMoveException {
        ChessBoardModel position = new ChessBoardModel();
        position.setPosition(chessRules.getInitialPosition());

        assertFalse(chessRules.getAvailableMoves(position).contains(Castling.CASTLEWHITEK));
        position.clearSquare(new ChessBoardCoord("f1"));
        assertFalse(chessRules.getAvailableMoves(position).contains(Castling.CASTLEWHITEK));
        position.clearSquare(new ChessBoardCoord("g1"));
        assertTrue(chessRules.getAvailableMoves(position).contains(Castling.CASTLEWHITEK));
        position.setPiece(new ChessBoardCoord("e2"), new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK));
        assertFalse(chessRules.getAvailableMoves(position).contains(Castling.CASTLEWHITEK));
    }
}
