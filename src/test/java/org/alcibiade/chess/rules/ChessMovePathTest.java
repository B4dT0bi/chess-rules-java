package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPieceType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class ChessMovePathTest {

    @Test
    public void testConstruction() {
        ChessMovePath p1 = new ChessMovePath("e2", "e4");
        ChessMovePath p2 = new ChessMovePath("e2", "e4", ChessPieceType.QUEEN);
        ChessMovePath p3 = new ChessMovePath("e2", "e4", ChessPieceType.KNIGHT);
        ChessMovePath p4 = new ChessMovePath("e2", "e3", ChessPieceType.QUEEN);

        assertTrue(p1.equals(p2));
        assertEquals(p1.hashCode(), p2.hashCode());
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
    }
}
