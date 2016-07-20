package org.alcibiade.chess.persistence.pgn;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class RoundTag extends PgnTag {
    public RoundTag() {
        super(TAG_ID_ROUND);
    }

    public RoundTag(final String round) {
        super(TAG_ID_ROUND);
        setRound(round);
    }

    public void setRound(final String round) {
        value = (round == null || round.isEmpty()) ? "?" : round;
    }
}
