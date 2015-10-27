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

    private String event;
    private String site;
    private String round;

    private List<String> moves;

    public PgnGameModel(
            String whitePlayerName, String blackPlayerName, Date gameDate, String result,
            String event, String site, String round,
            List<String> moves) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.gameDate = gameDate;
        this.moves = moves;
        this.result = result;
        this.event = event;
        this.site = site;
        this.round = round;
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

    public String getEvent() {
        return event;
    }

    public String getSite() {
        return site;
    }

    public String getRound() {
        return round;
    }

    @Override
    public String toString() {
        return String.format("%s vs. %s (%d moves, result: %s)", whitePlayerName, blackPlayerName, moves.size(), result);
    }

}
