package org.alcibiade.chess.persistence.pgn.optional;

import org.alcibiade.chess.persistence.pgn.PgnTag;

/**
 * Created by b4dt0bi on 26.07.16.
 */
public class WhiteElo extends PgnTag {
    public WhiteElo() {
        super("WhiteElo");
    }

    public WhiteElo(Integer value) {
        super("WhiteElo", value == null ? "-" : "" + value);
    }
}
