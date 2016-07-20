package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.AutoUpdateChessBoardModel;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.rules.Castling;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.ChessRulesImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class SanHelperTest {
    @Test
    public void test() {
        Assertions.assertThat(SanHelper.getSanMove(null, null, Castling.CASTLEBLACKK)).isEqualTo("O-O");
        Assertions.assertThat(SanHelper.getSanMove(null, null, Castling.CASTLEBLACKQ)).isEqualTo("O-O-O");
        Assertions.assertThat(SanHelper.getSanMove(null, null, Castling.CASTLEWHITEK)).isEqualTo("O-O");
        Assertions.assertThat(SanHelper.getSanMove(null, null, Castling.CASTLEWHITEQ)).isEqualTo("O-O-O");

        ChessRules rules = new ChessRulesImpl();
        AutoUpdateChessBoardModel model = new AutoUpdateChessBoardModel(rules);
        model.setInitialPosition();
        Assertions.assertThat(SanHelper.getSanMove(rules, model, new ChessMovePath("e2", "e4"))).isEqualTo("e4");
        model.update("e2e4");
        Assertions.assertThat(SanHelper.getSanMove(rules, model, new ChessMovePath("d7", "d5"))).isEqualTo("d5");
        model.update("d7d5");
        Assertions.assertThat(SanHelper.getSanMove(rules, model, new ChessMovePath("e4", "d5"))).isEqualTo("exd5");
        model.update("e4d5");
        Assertions.assertThat(SanHelper.getSanMove(rules, model, new ChessMovePath("g8", "f6"))).isEqualTo("Nf6");
        model.update("g8f6");
        Assertions.assertThat(SanHelper.getSanMove(rules, model, new ChessMovePath("f1", "b5"))).isEqualTo("Bb5+");
        model.update("f1b5");
        Assertions.assertThat(SanHelper.getSanMove(rules, model, new ChessMovePath("c8", "d7"))).isEqualTo("Bd7");
        model.update("c8d7");
        Assertions.assertThat(SanHelper.getSanMove(rules, model, new ChessMovePath("b5", "d7"))).isEqualTo("Bxd7+");
        model.update("b5d7");
        Assertions.assertThat(SanHelper.getSanMove(rules, model, new ChessMovePath("f6", "d7"))).isEqualTo("Nfxd7");
    }

    @Test
    public void testSanToLan() {
        ChessRules rules = new ChessRulesImpl();
        AutoUpdateChessBoardModel model = new AutoUpdateChessBoardModel(rules);
        model.setInitialPosition();
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "e4")).isEqualTo("e2e4");
        model.update("e2e4");
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "d5")).isEqualTo("d7d5");
        model.update("d7d5");
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "exd5")).isEqualTo("e4d5");
        model.update("e4d5");
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "Nf6")).isEqualTo("g8f6");
        model.update("g8f6");
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "Bb5+")).isEqualTo("f1b5");
        model.update("f1b5");
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "Bd7")).isEqualTo("c8d7");
        model.update("c8d7");
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "Bxd7+")).isEqualTo("b5d7");
        model.update("b5d7");
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "Nfxd7")).isEqualTo("f6d7");
    }

    @Test
    public void testRhe1() {
        ChessRules rules = new ChessRulesImpl();
        AutoUpdateChessBoardModel model = new AutoUpdateChessBoardModel(rules);
        model.setPosition(new FenChessPosition("r3k2r/pbqnbp1p/1pp1p3/5p2/2BP4/P1N2QN1/1PP2PPP/2KR3R w kq - 4 14"));
        Assertions.assertThat(SanHelper.convertSanToLan(rules, model, "Rhe1")).isEqualTo("h1e1");
    }
}
