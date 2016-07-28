package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPieceType;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import static org.junit.Assert.*;

public class ChessMovePathTest {

    @Test
    public void testConstruction() {
        ChessMovePath p1 = new ChessMovePath("e2", "e4");
        ChessMovePath p2 = new ChessMovePath("e2", "e4", ChessPieceType.QUEEN);
        ChessMovePath p3 = new ChessMovePath("e2", "e4", ChessPieceType.KNIGHT);
        ChessMovePath p4 = new ChessMovePath("e2", "e3", ChessPieceType.QUEEN);
        ChessMovePath p5 = new ChessMovePath("e2", "e4", "");
        ChessMovePath p6 = new ChessMovePath("e2e4");
        ChessMovePath p2a = new ChessMovePath("e2e4q");
        ChessMovePath p3a = new ChessMovePath("e2e4n");

        assertTrue(p1.equals(p2));
        assertEquals(p1.hashCode(), p2.hashCode());
        assertFalse(p1.equals(p3));
        assertFalse(p1.equals(p4));
        Assertions.assertThat(p5).isEqualToComparingFieldByField(p6);
        Assertions.assertThat(p2).isEqualToComparingFieldByField(p2a);
        Assertions.assertThat(p3).isEqualToComparingFieldByField(p3a);
    }
}
