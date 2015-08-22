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

        ChessBoardCoord e4Coord = new ChessBoardCoord(5, 1);
        ChessBoardCoord e4Offset = new ChessBoardCoord(13);
        Assertions.assertThat(e4Coord).isEqualToComparingFieldByField(e4Offset);
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
}

