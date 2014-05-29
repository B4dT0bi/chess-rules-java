package org.alcibiade.chess.engine;

import org.alcibiade.chess.model.ChessException;

public class ChessEngineFailureException extends ChessException {

    public ChessEngineFailureException(Throwable cause) {
        super("Chess engine failure", cause);
    }

    public ChessEngineFailureException(String message) {
        super(message);
    }
}
