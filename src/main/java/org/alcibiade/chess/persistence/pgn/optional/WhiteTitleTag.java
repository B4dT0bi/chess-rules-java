package org.alcibiade.chess.persistence.pgn.optional;

import org.alcibiade.chess.persistence.pgn.PgnTag;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class WhiteTitleTag extends PgnTag {
    public WhiteTitleTag() {
        super("WhiteTitle");
    }

    public WhiteTitleTag(final String title) {
        super("WhiteTitle");
        setTitle(title);
    }

    public void setTitle(final String title) {
        value = (title == null || title.isEmpty()) ? "-" : title;
    }

}
