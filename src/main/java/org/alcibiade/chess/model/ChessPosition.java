package org.alcibiade.chess.model;

public interface ChessPosition {

    ChessPiece getPiece(ChessBoardCoord coord);

    boolean isCastlingAvailable(ChessSide side, boolean kingside);

    ChessSide getNextPlayerTurn();

    ChessBoardCoord getLastPawnDMove();
}
