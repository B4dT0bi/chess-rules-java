package org.alcibiade.chess.persistence;

import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.ChessRulesImpl;
import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Tobias Boese <tobias.boese@gmail.com>
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

    @Test
    public void testVariations() throws IOException {
        PgnBookReader bookReader = new PgnBookReader(getClass().getResourceAsStream("../integration/variations.pgn"), rules);
        PgnGameModel gameModel = bookReader.readGame();
        Assertions.assertThat(gameModel).isNotNull();
        Assertions.assertThat(gameModel.getWhitePlayerName()).isEqualTo("Mate in one");
        Assertions.assertThat(gameModel.getPosition()).isNotNull();
    }

    @Test
    public void testVariations2() throws IOException {
        PgnBookReader bookReader = new PgnBookReader(getClass().getResourceAsStream("../integration/variation_new_line.pgn"), rules);
        PgnGameModel gameModel = bookReader.readGame();
        Assertions.assertThat(gameModel).isNotNull();
        Assertions.assertThat(gameModel.getWhitePlayerName()).isEqualTo("Mate in two");
        Assertions.assertThat(gameModel.getPosition()).isNotNull();
        List<String> expected = new ArrayList<>();
        expected.add("Kc3");
        expected.add("Ka2");
        expected.add("Qb2#");
        Assertions.assertThat(gameModel.getMoves()).isEqualTo(expected);
    }

    @Test
    public void testVariations3() throws IOException {
        PgnBookReader bookReader = new PgnBookReader(getClass().getResourceAsStream("../integration/variation_multiple_lines.pgn"), rules);
        PgnGameModel gameModel = bookReader.readGame();
        Assertions.assertThat(gameModel).isNotNull();
        Assertions.assertThat(gameModel.getWhitePlayerName()).isEqualTo("Mate in two");
        Assertions.assertThat(gameModel.getPosition()).isNotNull();
        List<String> expected = new ArrayList<>();
        expected.add("Nf7+");
        expected.add("Kh7");
        expected.add("Bxd3#");
        Assertions.assertThat(gameModel.getMoves()).isEqualTo(expected);
    }

    @Test
    public void testNonAmbiguousCauseOfCheck() throws IOException {
        PgnBookReader bookReader = new PgnBookReader(getClass().getResourceAsStream("../integration/non_ambiguous_cause_of_check.pgn"), rules);
        PgnGameModel gameModel = bookReader.readGame();
        Assertions.assertThat(gameModel).isNotNull();
        Assertions.assertThat(gameModel.getPosition()).isNotNull();
        List<String> expected = new ArrayList<>();
        expected.add("Qg7+");
        expected.add("Rxg7");
        expected.add("fxg7#");
        Assertions.assertThat(gameModel.getMoves()).isEqualTo(expected);
    }

    @Test
    public void testNonAmbiguousCauseOfCheck2() throws IOException {
        PgnBookReader bookReader = new PgnBookReader(getClass().getResourceAsStream("../integration/non_ambiguous_cause_of_check2.pgn"), rules);
        PgnGameModel gameModel = bookReader.readGame();
        Assertions.assertThat(gameModel).isNotNull();
        Assertions.assertThat(gameModel.getPosition()).isNotNull();
        List<String> expected = new ArrayList<>();
        expected.add("f3");
        expected.add("Nf4");
        expected.add("Nf5#");
        Assertions.assertThat(gameModel.getMoves()).isEqualTo(expected);
    }

}