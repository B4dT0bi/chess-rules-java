package org.alcibiade.chess.persistence.pgn.optional;

import org.alcibiade.chess.persistence.pgn.PgnTag;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class PlyCountTag extends PgnTag {
    public PlyCountTag() {
        super("PlyCount");
    }

    public PlyCountTag(final Integer plyCount) {
        super("PlyCount");
        setPlyCount(plyCount);
    }

    public void setPlyCount(final Integer plyCount) {
        value = plyCount.toString();
    }

}
