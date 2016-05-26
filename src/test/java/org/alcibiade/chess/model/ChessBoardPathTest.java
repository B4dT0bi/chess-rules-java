package org.alcibiade.chess.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ChessBoardPathTest {

    @Test
    public void testConstructor() {
        new ChessBoardPath();
    }

    @Test
    public void testVerticalPath() {
        ChessBoardPath path = new ChessBoardPath("e2", "e4");
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
        ChessBoardPath path = new ChessBoardPath("e2", "h2");
        Assertions.assertThat(path.get4Distance()).isEqualTo(3);
        Assertions.assertThat(path.get8Distance()).isEqualTo(3);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f2"))).isTrue();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e3"))).isFalse();
    }

    @Test
    public void testDiagonalPath() {
        ChessBoardPath path = new ChessBoardPath("e2", "h5");
        Assertions.assertThat(path.get4Distance()).isEqualTo(6);
        Assertions.assertThat(path.get8Distance()).isEqualTo(3);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f3"))).isTrue();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("d3"))).isFalse();
    }

    @Test
    public void testLPathVertical() {
        ChessBoardPath path = new ChessBoardPath("e2", "f4");
        Assertions.assertThat(path.get4Distance()).isEqualTo(3);
        Assertions.assertThat(path.get8Distance()).isEqualTo(2);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f3"))).isFalse();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("e3"))).isTrue();
    }

    @Test
    public void testLPathHorizontal() {
        ChessBoardPath path = new ChessBoardPath("e2", "g1");
        Assertions.assertThat(path.get4Distance()).isEqualTo(3);
        Assertions.assertThat(path.get8Distance()).isEqualTo(2);
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f1"))).isFalse();
        Assertions.assertThat(path.isOverlapping(new ChessBoardCoord("f2"))).isTrue();
    }
}
