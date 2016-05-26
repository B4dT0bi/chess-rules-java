package org.alcibiade.chess.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ChessSideTest {

    @Test
    public void testSide() {
        Assertions.assertThat(ChessSide.BLACK.getFullName()).isEqualTo("black");
    }
}