package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.AutoUpdateChessBoardModel;
import org.alcibiade.chess.persistence.pgn.PgnTag;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.ChessRulesImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Tobias Boese <tobias.boese@gmail.com>
 */
public class PgnMarshallerTest {
    @Test
    public void testGameDump() {
        ChessRules rules = new ChessRulesImpl();
        AutoUpdateChessBoardModel autoUpdateChessBoardModel = new AutoUpdateChessBoardModel(rules);
        autoUpdateChessBoardModel.setInitialPosition();
        autoUpdateChessBoardModel.update("e2e4");
        autoUpdateChessBoardModel.update("d7d5");
        autoUpdateChessBoardModel.update("e4d5");
        autoUpdateChessBoardModel.update("g8f6");
        autoUpdateChessBoardModel.update("f1b5");
        autoUpdateChessBoardModel.update("c8d7");
        autoUpdateChessBoardModel.update("b5d7");
        autoUpdateChessBoardModel.update("b8d7");
        autoUpdateChessBoardModel.update("b1c3");
        autoUpdateChessBoardModel.update("d7b6");
        autoUpdateChessBoardModel.update("g1f3");
        autoUpdateChessBoardModel.update("b6d5");
        autoUpdateChessBoardModel.update("e1g1");
        autoUpdateChessBoardModel.update("e7e6");

        String pgnGame = new PgnMarshallerImpl().exportGame(new ArrayList<PgnTag>(), autoUpdateChessBoardModel);
        System.out.println(pgnGame);
    }

}
