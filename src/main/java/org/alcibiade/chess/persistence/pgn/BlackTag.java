package org.alcibiade.chess.persistence.pgn;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class BlackTag extends PgnTag {
    public BlackTag() {
        super(TAG_ID_BLACK);
    }

    public BlackTag(final String blackPlayer) {
        super(TAG_ID_BLACK);
        setPlayerName(blackPlayer);
    }

    public void setPlayerName(final String playerName) {
        value = (playerName == null || playerName.isEmpty()) ? "?" : playerName;
    }
}
