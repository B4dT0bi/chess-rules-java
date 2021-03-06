package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.ChessMovePath;

public class Castling {

    public static final ChessMovePath CASTLEWHITEK = new ChessMovePath("e1", "g1");
    public static final ChessMovePath CASTLEWHITEQ = new ChessMovePath("e1", "c1");
    public static final ChessMovePath CASTLEBLACKK = new ChessMovePath("e8", "g8");
    public static final ChessMovePath CASTLEBLACKQ = new ChessMovePath("e8", "c8");

    private Castling() {
        // Don't instantiate this class
    }
}
