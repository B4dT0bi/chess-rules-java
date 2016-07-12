package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.persistence.FenChessPosition;
import org.junit.Assert;
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

    @Test
    public void testCastlingMove() {
        ChessRules rules = new ChessRulesImpl();
        ChessBoardModel chessBoardModel = new ChessBoardModel();
        chessBoardModel.setPosition(new FenChessPosition("rnbqk2r/pp2ppbp/6p1/2p5/3P4/2PBPN2/P4PPP/R1BQK2R b KQkq - 2 8"));
        chessBoardModel = ChessHelper.applyMoveAndSwitch(rules, chessBoardModel, new ChessMovePath("e8", "g8"));
        Assert.assertFalse(chessBoardModel.isCastlingAvailable(ChessSide.BLACK, true));
        Assert.assertFalse(chessBoardModel.isCastlingAvailable(ChessSide.BLACK, false));
        Assert.assertTrue(chessBoardModel.isCastlingAvailable(ChessSide.WHITE, true));
        Assert.assertTrue(chessBoardModel.isCastlingAvailable(ChessSide.WHITE, false));
    }
}
