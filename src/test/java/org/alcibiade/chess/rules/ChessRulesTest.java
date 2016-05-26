package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.IllegalMoveException;
import org.junit.Test;

public class ChessRulesTest {

    /**
     * Move kingside rook back and forth, and then try to castle.
     */
    @Test(expected = IllegalMoveException.class)
    public void testCastlingValidation() {
        ChessRulesImpl rules = new ChessRulesImpl();
        ChessPosition position = rules.getInitialPosition();

        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("e2", "e4"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("e7", "e5"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("f1", "e2"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("d7", "d5"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("g1", "f3"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("c7", "c5"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("h1", "g1"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("b7", "b5"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("g1", "h1"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("a7", "a5"));

        // Castling while rook has already moved
        ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("e1", "g1"));
    }
}
