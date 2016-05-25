package org.alcibiade.chess.model;

public interface ChessPosition {

    /**
     * Get the move number. It starts at 1, and is incremented after Black's move.
     *
     * @return the move number.
     */
    int getMoveNumber();

    /**
     * This is the number of halfmoves since the last capture or pawn advance. This is used to
     * determine if a draw can be claimed under the fifty-move rule.
     *
     * @return the halfmove clock
     */
    int getHalfMoveClock();

    /**
     * Get a piece from the board.
     *
     * @param coordinates the piece coordinates
     * @return the piece at the coordinates, or null if the square is empty.
     */
    ChessPiece getPiece(ChessBoardCoord coordinates);

    /**
     * Check if castling is available at the current position. It does not check if the king can actually
     * castle on the next move but rather if it can castle based on potential previous movements.
     *
     * @param side     which player should we check (may not be the next player of the position)
     * @param kingside which castling side ? True for king side, false for queen side.
     * @return true if castling is possible
     */
    boolean isCastlingAvailable(ChessSide side, boolean kingside);

    /**
     * Get the next player to move.
     *
     * @return the side of the next player.
     */
    ChessSide getNextPlayerTurn();

    /**
     * If a pawn has just moved two squares ahead, this will be the destination of this pawn.
     *
     * @return the destination of the pawn, or null if last move was not a two squares pawn move.
     */
    ChessBoardCoord getLastPawnDMove();
}
