package org.alcibiade.chess.persistence;

import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Model mapped to a typical PGN game file.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
public class PgnGameModel {

    private String whitePlayerName;
    private String blackPlayerName;
    private Date gameDate;
    private String result;
    private List<String> moves;

    public PgnGameModel(String whitePlayerName, String blackPlayerName, Date gameDate, String result, List<String> moves) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.gameDate = gameDate;
        this.moves = moves;
        this.result = result;
    }

    public List<String> getMoves() {
        return Collections.unmodifiableList(moves);
    }

    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    public Date getGameDate() {
        return gameDate;
    }

    public String getResult() {
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s vs. %s (%d moves, result: %s)", whitePlayerName, blackPlayerName, moves.size(), result);
    }

}
