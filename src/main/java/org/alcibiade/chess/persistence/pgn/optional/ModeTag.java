package org.alcibiade.chess.persistence.pgn.optional;

import org.alcibiade.chess.persistence.pgn.PgnTag;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class ModeTag extends PgnTag {
    public enum Mode {
        OVER_THE_BOARD("OTB"),
        PAPER_MAIL("PM"),
        ELECTRONIC_MAIL("EM"),
        INTERNET_CHESS_SERVER("ICS"),
        GENERAL_TELECOMUNICATION("TC");

        private String pgnValue;

        Mode(String pgnValue) {
            this.pgnValue = pgnValue;
        }

        public String getPgnValue() {
            return pgnValue;
        }

        public static Mode getByPgn(String pgn) {
            for (Mode result : values()) {
                if (result.pgnValue.equals(pgn)) return result;
            }
            return null;
        }
    }

    public ModeTag() {
        super("Mode");
    }

    public ModeTag(final String result) {
        super("Mode");
        setMode(result);
    }

    public ModeTag(final Mode mode) {
        super("Mode");
        setMode(mode);
    }

    public void setMode(final Mode mode) {
        value = (mode == null) ? Mode.GENERAL_TELECOMUNICATION.getPgnValue() : mode.getPgnValue();
    }

    public void setMode(final String result) {
        setMode(Mode.getByPgn(result));
    }
}
