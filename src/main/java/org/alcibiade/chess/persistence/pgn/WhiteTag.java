package org.alcibiade.chess.persistence.pgn;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class WhiteTag extends PgnTag {
    public WhiteTag() {
        super(TAG_ID_WHITE);
    }

    public WhiteTag(final String whitePlayer) {
        super(TAG_ID_WHITE);
        setPlayerName(whitePlayer);
    }

    public void setPlayerName(final String playerName) {
        value = (playerName == null || playerName.isEmpty()) ? "?" : playerName;
    }
}
