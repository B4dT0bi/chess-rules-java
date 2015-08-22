package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.*;
import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class PieceMoveManagerTest {

    private ChessRules chessRules = new ChessRulesImpl();

    @Test
    public void testGetReachableForPawn() {
        ChessBoardModel model = new ChessBoardModel();

        model.setInitialPosition();
        model.setPiece(new ChessBoardCoord(5, 2), new ChessPiece(ChessPieceType.KNIGHT,
                ChessSide.BLACK));
        model.setPiece(new ChessBoardCoord(7, 2), new ChessPiece(ChessPieceType.KNIGHT,
                ChessSide.WHITE));
        PieceMoveManager manager = new PieceMoveManager(model);
        Set<ChessBoardCoord> reachableSquares = manager.getReachableSquares(
                new ChessBoardCoord(6, 1), chessRules);

        assertTrue(reachableSquares.contains(new ChessBoardCoord(6, 2)));
        assertTrue(reachableSquares.contains(new ChessBoardCoord(6, 3)));
        assertTrue(reachableSquares.contains(new ChessBoardCoord(5, 2)));
        assertEquals(3, reachableSquares.size());
    }

    @Test
    public void testGetReachableForKnight() {
        ChessBoardModel model = new ChessBoardModel();
        model.setInitialPosition();

        PieceMoveManager manager = new PieceMoveManager(model);
        Set<ChessBoardCoord> reachableSquares = manager.getReachableSquares(
                new ChessBoardCoord(6, 0), chessRules);

        assertTrue(reachableSquares.contains(new ChessBoardCoord(7, 2)));
        assertTrue(reachableSquares.contains(new ChessBoardCoord(5, 2)));
        assertEquals(2, reachableSquares.size());
    }

    @Test
    public void testGetReachableForKing() {
        ChessBoardModel model = new ChessBoardModel();
        model.setPiece(new ChessBoardCoord(4, 2),
                new ChessPiece(ChessPieceType.KING, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(4, 3),
                new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(5, 1),
                new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK));

        PieceMoveManager manager = new PieceMoveManager(model);
        Set<ChessBoardCoord> reachableSquares = manager.getReachableSquares(
                new ChessBoardCoord(4, 2), chessRules);

        assertEquals(7, reachableSquares.size());
    }

    @Test
    public void testGetReachableForRook() {
        ChessBoardModel model = new ChessBoardModel();
        model.setPiece(new ChessBoardCoord(4, 2),
                new ChessPiece(ChessPieceType.ROOK, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(2, 2),
                new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(4, 6),
                new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK));

        PieceMoveManager manager = new PieceMoveManager(model);
        Set<ChessBoardCoord> reachableSquares = manager.getReachableSquares(
                new ChessBoardCoord(4, 2), chessRules);

        assertEquals(10, reachableSquares.size());
    }

    @Test
    public void testGetReachableForBishop() {
        ChessBoardModel model = new ChessBoardModel();
        model.setPiece(new ChessBoardCoord(4, 2),
                new ChessPiece(ChessPieceType.BISHOP, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(2, 2),
                new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(4, 6),
                new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK));

        PieceMoveManager manager = new PieceMoveManager(model);
        Set<ChessBoardCoord> reachableSquares = manager.getReachableSquares(
                new ChessBoardCoord(4, 2), chessRules);

        assertEquals(11, reachableSquares.size());
    }

    @Test
    public void testGetReachableForQueen() {
        ChessBoardModel model = new ChessBoardModel();
        model.setPiece(new ChessBoardCoord(4, 2),
                new ChessPiece(ChessPieceType.QUEEN, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(2, 2),
                new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(4, 6),
                new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK));

        PieceMoveManager manager = new PieceMoveManager(model);
        Set<ChessBoardCoord> reachableSquares = manager.getReachableSquares(
                new ChessBoardCoord(4, 2), chessRules);

        assertEquals(21, reachableSquares.size());

    }
}
