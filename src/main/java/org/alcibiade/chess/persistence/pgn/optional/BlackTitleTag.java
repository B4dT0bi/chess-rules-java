package org.alcibiade.chess.persistence.pgn.optional;

import org.alcibiade.chess.persistence.pgn.PgnTag;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class BlackTitleTag extends PgnTag {
    public BlackTitleTag() {
        super("BlackTitle");
    }

    public BlackTitleTag(final String title) {
        super("BlackTitle");
        setTitle(title);
    }

    public void setTitle(final String title) {
        value = (title == null || title.isEmpty()) ? "-" : title;
    }

}
