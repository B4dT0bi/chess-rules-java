package org.alcibiade.chess.persistence;


import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

public class PgnGameModelTest {

    @Test
    public void testAttributes() {
        Date date = new Date();
        PgnGameModel model1 = new PgnGameModel("W", "B", date, "", "", "", "", new ArrayList<String>());
        PgnGameModel model2 = new PgnGameModel("W", "B", date, "", "", "", "", new ArrayList<String>());
        PgnGameModel model3 = new PgnGameModel("X", "B", date, "", "", "", "", new ArrayList<String>());

        Assertions.assertThat(model1).isNotEqualTo(model2);
        Assertions.assertThat(model1.hashCode()).isNotEqualTo(model2.hashCode());
        Assertions.assertThat(model1).isEqualToComparingFieldByField(model2);

        Assertions.assertThat(model1).isNotEqualTo(model3);
        Assertions.assertThat(model1.hashCode()).isNotEqualTo(model3.hashCode());
    }
}