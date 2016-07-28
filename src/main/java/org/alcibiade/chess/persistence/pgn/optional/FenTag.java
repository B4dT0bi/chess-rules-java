package org.alcibiade.chess.persistence.pgn.optional;

import org.alcibiade.chess.persistence.pgn.PgnTag;

/**
 * Created by b4dt0bi on 26.07.16.
 */
public class FenTag extends PgnTag {
    public FenTag() {
        super("FEN");
    }

    public FenTag(String value) {
        super("FEN", value);
    }
}
