package org.alcibiade.chess.model;

/**
 * Ancestor exception for all concrete model-related exception.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
public abstract class ChessException extends Exception {

    public ChessException(String message) {
        super(message);
    }

    public ChessException(String message, Throwable cause) {
        super(message, cause);
    }
}
