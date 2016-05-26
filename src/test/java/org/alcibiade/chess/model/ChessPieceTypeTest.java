package org.alcibiade.chess.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ChessPieceTypeTest {

    @Test
    public void testPieceType() {
        Assertions.assertThat(ChessPieceType.KNIGHT.getFullName()).isEqualTo("knight");
    }
}
