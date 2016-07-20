package org.alcibiade.chess.persistence;

import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.ChessRulesImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by b4dt0bi on 20.07.16.
 */
public class PgnBookReaderTest {
    ChessRules rules = new ChessRulesImpl();

    @Test
    public void testCommentsWithoutRules() throws IOException {
        PgnBookReader bookReader = new PgnBookReader(getClass().getResourceAsStream("../integration/comments.pgn"));
        PgnGameModel gameModel = bookReader.readGame();
        System.out.println(gameModel);
        Assertions.assertThat(gameModel).isNotNull();
        Assertions.assertThat(gameModel.getWhitePlayerName()).isEqualTo("Jan Willem te Kolste");
        Assertions.assertThat(gameModel.getBlackPlayerName()).isEqualTo("Carlos Torre Repetto");
        Assertions.assertThat(gameModel.getPosition()).isNull();
    }

    @Test
    public void testCommentsWithRules() throws IOException {
        PgnBookReader bookReader = new PgnBookReader(getClass().getResourceAsStream("../integration/comments.pgn"), rules);
        PgnGameModel gameModel = bookReader.readGame();
        Assertions.assertThat(gameModel).isNotNull();
        Assertions.assertThat(gameModel.getWhitePlayerName()).isEqualTo("Jan Willem te Kolste");
        Assertions.assertThat(gameModel.getBlackPlayerName()).isEqualTo("Carlos Torre Repetto");
        Assertions.assertThat(gameModel.getPosition()).isNotNull();
    }

    @Test
    public void testMultipleGames() throws IOException {
        PgnBookReader bookReader = new PgnBookReader(getClass().getResourceAsStream("../integration/multiple_games.pgn"), rules);
        PgnGameModel gameModel = bookReader.readGame();
        Assertions.assertThat(gameModel).isNotNull();
        Assertions.assertThat(gameModel.getWhitePlayerName()).isEqualTo("Fischer, Robert J.");
        Assertions.assertThat(gameModel.getBlackPlayerName()).isEqualTo("Spassky, Boris V.");
        Assertions.assertThat(gameModel.getPosition()).isNotNull();

        PgnGameModel gameModel2 = bookReader.readGame();
        Assertions.assertThat(gameModel2).isNotNull();
        Assertions.assertThat(gameModel2.getBlackPlayerName()).isEqualTo("GNU Chess 6.1.1");
        Assertions.assertThat(gameModel2.getPosition()).isNotNull();
        Assertions.assertThat(gameModel2).isNotEqualTo(gameModel);
    }
}
