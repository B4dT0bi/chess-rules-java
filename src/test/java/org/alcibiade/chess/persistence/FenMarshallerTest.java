package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.ChessRulesImpl;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;

/**
 * Marshalling tests.
 */
public class FenMarshallerTest {
    private Logger logger = LoggerFactory.getLogger(FenMarshallerTest.class);

    @Test
    public void testTransformation() {
        ChessRules rules = new ChessRulesImpl();
        PgnMarshaller pgnMarshaller = new PgnMarshallerImpl(rules);
        FenMarshallerImpl positionMarshaller = new FenMarshallerImpl();

        ChessBoardModel chessBoardModel = new ChessBoardModel();
        chessBoardModel.setInitialPosition();
        String initialPositionAsFen = positionMarshaller.convertPositionToString(chessBoardModel);
        Assert.assertEquals(initialPositionAsFen, "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1");

        ChessBoardModel chessBoardModelFromFen = new ChessBoardModel();
        chessBoardModelFromFen.setPosition(positionMarshaller.convertStringToPosition(initialPositionAsFen));
        Assert.assertEquals(chessBoardModel, chessBoardModelFromFen);

        ChessBoardModel positionE4 = applyAndSwitch(rules, pgnMarshaller, chessBoardModel, "e4");
        Assertions.assertThat(positionMarshaller.convertPositionToString(positionE4)).isEqualTo("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1");
        chessBoardModelFromFen.setPosition(positionMarshaller.convertStringToPosition("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR b KQkq e3 0 1"));
        Assert.assertEquals(positionE4, chessBoardModelFromFen);

        ChessBoardModel positionC5 = applyAndSwitch(rules, pgnMarshaller, positionE4, "c5");
        Assertions.assertThat(positionMarshaller.convertPositionToString(positionC5)).isEqualTo("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2");
        chessBoardModelFromFen.setPosition(positionMarshaller.convertStringToPosition("rnbqkbnr/pp1ppppp/8/2p5/4P3/8/PPPP1PPP/RNBQKBNR w KQkq c6 0 2"));
        Assert.assertEquals(positionC5, chessBoardModelFromFen);

        ChessBoardModel positionNf3 = applyAndSwitch(rules, pgnMarshaller, positionC5, "Nf3");
        Assertions.assertThat(positionMarshaller.convertPositionToString(positionNf3)).isEqualTo("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2");
        chessBoardModelFromFen.setPosition(positionMarshaller.convertStringToPosition("rnbqkbnr/pp1ppppp/8/2p5/4P3/5N2/PPPP1PPP/RNBQKB1R b KQkq - 1 2"));
        Assert.assertEquals(positionNf3, chessBoardModelFromFen);
    }

    private ChessBoardModel applyAndSwitch(ChessRules rules, PgnMarshaller pgnMarshaller, ChessPosition position, String pgnMove) {
        ChessMovePath move = pgnMarshaller.convertPgnToMove(position, pgnMove);

        ChessBoardModel nextPosition = new ChessBoardModel();
        nextPosition.setPosition(position);

        Collection<ChessBoardUpdate> updates = rules.getUpdatesForMove(position, move);
        for (ChessBoardUpdate update : updates) {
            logger.debug("{} move {}: {}", position.getNextPlayerTurn(), pgnMove, update);
            update.apply(nextPosition);
        }

        ChessBoardModel model = nextPosition;
        model.nextPlayerTurn();

        return model;
    }
}