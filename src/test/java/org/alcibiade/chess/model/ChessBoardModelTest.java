package org.alcibiade.chess.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

public class ChessBoardModelTest {

    @Test
    public void testBoardModel() {
        ChessBoardModel model1 = new ChessBoardModel();
        ChessBoardModel model2 = new ChessBoardModel();

        model1.setInitialPosition();
        model2.setPosition(model1);

        Assertions.assertThat(model1).isEqualTo(model2);
        Assertions.assertThat(model1.hashCode()).isEqualTo(model2.hashCode());

        model2.movePiece(new ChessBoardCoord("e2"), new ChessBoardCoord("e4"));
        Assertions.assertThat(model1).isNotEqualTo(model2);
        Assertions.assertThat(model1.hashCode()).isNotEqualTo(model2.hashCode());
    }
}
