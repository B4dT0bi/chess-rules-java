package org.alcibiade.chess.persistence.pgn;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class ResultTag extends PgnTag {
    public enum Result {
        WHITE_HAS_WON("1-0"),
        BLACK_HAS_WON("0-1"),
        DRAW("1/2-1/2"),
        OPEN("*");

        private String pgnValue;

        Result(String pgnValue) {
            this.pgnValue = pgnValue;
        }

        public String getPgnValue() {
            return pgnValue;
        }

        public static Result getByPgn(String pgn) {
            for (Result result : values()) {
                if (result.pgnValue.equals(pgn)) return result;
            }
            return null;
        }
    }

    public ResultTag() {
        super(TAG_ID_RESULT);
    }

    public ResultTag(final String result) {
        super(TAG_ID_RESULT);
        setResult(result);
    }

    public ResultTag(final Result result) {
        super(TAG_ID_RESULT);
        setResult(result);
    }

    public void setResult(final Result result) {
        value = (result == null) ? Result.OPEN.getPgnValue() : result.getPgnValue();
    }

    public void setResult(final String result) {
        setResult(Result.getByPgn(result));
    }
}
