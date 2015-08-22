package org.alcibiade.chess.integration;

import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.PgnMoveException;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.alcibiade.chess.persistence.PositionMarshaller;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test position persistence.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class PositionMarshallerImplTest {

    @Autowired
    private ChessRules chessRules;

    @Autowired
    private PositionMarshaller positionMarshaller;

    @Autowired
    private PgnMarshaller pgnMarshaller;

    @Test
    public void testPositionToString() throws IllegalMoveException, PgnMoveException {
        ChessPosition position = chessRules.getInitialPosition();

        Assertions.assertThat(positionMarshaller.convertPositionToString(position))
                .isEqualTo("RNBQKBNRPPPPPPPP--------------------------------pppppppprnbqkbnrKQkq-");
        Assertions.assertThat(positionMarshaller.convertStringToPosition(
                "RNBQKBNRPPPPPPPP--------------------------------pppppppprnbqkbnrKQkq-"))
                .isEqualTo(position);

        position = ChessHelper.applyMoveAndSwitch(chessRules, position,
                pgnMarshaller.convertPgnToMove(position, "e4"));

        Assertions.assertThat(positionMarshaller.convertPositionToString(position))
                .isEqualTo("RNBQKBNRPPPP-PPP------------P-------------------pppppppprnbqkbnrKQkqe4");

        position = ChessHelper.applyMoveAndSwitch(chessRules, position,
                pgnMarshaller.convertPgnToMove(position, "Nf6"));

        Assertions.assertThat(positionMarshaller.convertPositionToString(position))
                .isEqualTo("RNBQKBNRPPPP-PPP------------P----------------n--pppppppprnbqkb-rKQkq-");

        position = ChessHelper.applyMoveAndSwitch(chessRules, position,
                pgnMarshaller.convertPgnToMove(position, "Ke2"));

        Assertions.assertThat(positionMarshaller.convertPositionToString(position))
                .isEqualTo("RNBQ-BNRPPPPKPPP------------P----------------n--pppppppprnbqkb-r--kq-");
    }
}
