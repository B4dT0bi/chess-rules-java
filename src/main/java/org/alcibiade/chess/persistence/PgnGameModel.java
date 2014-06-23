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
    private List<String> moves;

    public PgnGameModel(String whitePlayerName, String blackPlayerName, Date gameDate, List<String> moves) {
        this.whitePlayerName = whitePlayerName;
        this.blackPlayerName = blackPlayerName;
        this.gameDate = gameDate;
        this.moves = moves;
    }

    public List<String> getMoves() {
        return Collections.unmodifiableList(moves);
    }
}