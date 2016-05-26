package org.alcibiade.chess.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Unit tests for chess moves.
 */
public class ChessMovePathTest {

    @Test
    public void testVerticalPath() {
        ChessMovePath path = new ChessMovePath("e2", "e4");
        Assertions.assertThat(path.get4Distance()).isEqualTo(2);
        Assertions.assertThat(path.get8Distance()).isEqualTo(2);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e2"))).isTrue();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e3"))).isTrue();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e4"))).isTrue();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e1"))).isFalse();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e5"))).isFalse();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("d3"))).isFalse();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f3"))).isFalse();
    }

    @Test
    public void testHorizontalPath() {
        ChessMovePath path = new ChessMovePath("e2", "h2");
        Assertions.assertThat(path.get4Distance()).isEqualTo(3);
        Assertions.assertThat(path.get8Distance()).isEqualTo(3);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f2"))).isTrue();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e3"))).isFalse();
    }

    @Test
    public void testDiagonalPath() {
        ChessMovePath path = new ChessMovePath("e2", "h5");
        Assertions.assertThat(path.get4Distance()).isEqualTo(6);
        Assertions.assertThat(path.get8Distance()).isEqualTo(3);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f3"))).isTrue();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("d3"))).isFalse();
    }

    @Test
    public void testLPathVertical() {
        ChessMovePath path = new ChessMovePath("e2", "f4");
        Assertions.assertThat(path.get4Distance()).isEqualTo(3);
        Assertions.assertThat(path.get8Distance()).isEqualTo(2);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f3"))).isFalse();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e3"))).isTrue();
    }

    @Test
    public void testLPathHorizontal() {
        ChessMovePath path = new ChessMovePath("e2", "g1");
        Assertions.assertThat(path.get4Distance()).isEqualTo(3);
        Assertions.assertThat(path.get8Distance()).isEqualTo(2);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f1"))).isFalse();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f2"))).isTrue();
    }
}
