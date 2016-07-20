package org.alcibiade.chess.persistence.pgn.optional;

import org.alcibiade.chess.persistence.pgn.PgnTag;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class BlackTypeTag extends PgnTag {
    public BlackTypeTag() {
        super("BlackType");
    }

    public BlackTypeTag(final PlayerType type) {
        this();
        setType(type);
    }

    public void setType(final PlayerType type) {
        value = (type == null) ? PlayerType.HUMAN.name().toLowerCase() : type.name().toLowerCase();
    }

}
