package org.alcibiade.chess.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Test coordinates consistency.
 */
public class ChessBoardCoordTest {

    @Test
    public void testOffset() {
        ChessBoardCoord a1Coord = new ChessBoardCoord(0, 0);
        ChessBoardCoord a1Offset = new ChessBoardCoord(0);
        Assertions.assertThat(a1Coord).isEqualToComparingFieldByField(a1Offset);
        Assertions.assertThat(a1Coord.getOffset()).isEqualTo(0);

        ChessBoardCoord e4Coord = new ChessBoardCoord(5, 1);
        ChessBoardCoord e4Offset = new ChessBoardCoord(13);
        Assertions.assertThat(e4Coord).isEqualToComparingFieldByField(e4Offset);
        Assertions.assertThat(e4Coord.getOffset()).isEqualTo(13);
    }

    @Test
    public void testNaming() {
        ChessBoardCoord a1 = new ChessBoardCoord(0, 0);
        Assertions.assertThat(a1.getPgnCoordinates()).isEqualTo("a1");
        ChessBoardCoord e4 = new ChessBoardCoord(4, 3);
        Assertions.assertThat(e4.getPgnCoordinates()).isEqualTo("e4");
        ChessBoardCoord h8 = new ChessBoardCoord(7, 7);
        Assertions.assertThat(h8.getPgnCoordinates()).isEqualTo("h8");
    }

    @Test
    public void testAdd() {
        ChessBoardCoord c1 = new ChessBoardCoord(1, 2);
        ChessBoardCoord c2 = c1.add(new ChessBoardCoord(3, 1));
        Assertions.assertThat(c1.getPgnCoordinates()).isEqualTo("b3");
        Assertions.assertThat(c2.getPgnCoordinates()).isEqualTo("e4");
    }

    @Test
    public void testDefaultConstructor() {
        ChessBoardCoord c1 = new ChessBoardCoord();
        Assertions.assertThat(c1.getPgnCoordinates()).isEqualTo("a1");
    }
}

