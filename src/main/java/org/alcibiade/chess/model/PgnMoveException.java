package org.alcibiade.chess.model;

/**
 * Invalid PGN notation encountered.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
public class PgnMoveException extends ChessException {

    public PgnMoveException(String message) {
        super(message);
    }

    public PgnMoveException(String pgnMove, String message) {
        super(message + " for '" + pgnMove + "'");
    }
}
