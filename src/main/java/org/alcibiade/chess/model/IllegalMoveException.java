package org.alcibiade.chess.model;

/**
 * Illegal move performed.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
public class IllegalMoveException extends ChessException {

    public IllegalMoveException(ChessBoardCoord coord) {
        super("Illegal move at " + coord);
    }

    public IllegalMoveException(ChessMovePath path, String message) {
        super(message + " for " + path);
    }
}
