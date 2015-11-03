package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.PgnMoveException;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.ChessRulesImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Marshalling tests.
 */
public class PositionMarshallerTest {

    @Test
    public void testTransformation() throws PgnMoveException, IllegalMoveException {
        ChessRules rules = new ChessRulesImpl();
        PgnMarshaller pgnMarshaller = new PgnMarshallerImpl(rules);
        PositionMarshallerImpl positionMarshaller = new PositionMarshallerImpl();

        ChessPosition position1 = rules.getInitialPosition();
        String position1txt = positionMarshaller.convertPositionToString(position1);
        Assertions.assertThat(positionMarshaller.convertStringToPosition(position1txt)).isEqualToComparingFieldByField(position1);

        ChessMovePath path1 = pgnMarshaller.convertPgnToMove(position1, "e4");

        ChessPosition position2 = ChessHelper.applyMoveAndSwitch(rules, position1, path1);
        String position2txt = positionMarshaller.convertPositionToString(position2);
        Assertions.assertThat(positionMarshaller.convertStringToPosition(position2txt)).isEqualToComparingFieldByField(position2);
    }
}
