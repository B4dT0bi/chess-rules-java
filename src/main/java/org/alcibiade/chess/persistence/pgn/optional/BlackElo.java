package org.alcibiade.chess.persistence.pgn.optional;

import org.alcibiade.chess.persistence.pgn.PgnTag;

/**
 * Created by b4dt0bi on 26.07.16.
 */
public class BlackElo extends PgnTag {
    public BlackElo() {
        super("BlackElo");
    }

    public BlackElo(Integer value) {
        super("BlackElo", value == null ? "-" : "" + value);
    }
}
