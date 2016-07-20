package org.alcibiade.chess.model;

import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.ChessRulesImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Created by b4dt0bi on 13.07.16.
 */
public class AutoUpdateChessBoardModelTest {
    @Test
    public void test() {
        ChessRules rules = new ChessRulesImpl();
        AutoUpdateChessBoardModel model1 = new AutoUpdateChessBoardModel(rules);
        ChessBoardModel model2 = new ChessBoardModel();

        model1.setInitialPosition();
        model2.setPosition(model1);

        Assertions.assertThat(model1).isEqualTo(model2);
        Assertions.assertThat(model1.hashCode()).isEqualTo(model2.hashCode());

        model1.update("e2e4");
        Assertions.assertThat(model1).isNotEqualTo(model2);
        Assertions.assertThat(model1.hashCode()).isNotEqualTo(model2.hashCode());

        AutoUpdateChessBoardModel model3 = new AutoUpdateChessBoardModel(rules);
        model3.setPosition(model1);

        model1.prev();

        Assertions.assertThat(model1).isEqualTo(model2);
        Assertions.assertThat(model1.hashCode()).isEqualTo(model2.hashCode());

        model1.next();

        Assertions.assertThat(model1).isEqualTo(model3);
        Assertions.assertThat(model1.hashCode()).isEqualTo(model3.hashCode());
    }
}
