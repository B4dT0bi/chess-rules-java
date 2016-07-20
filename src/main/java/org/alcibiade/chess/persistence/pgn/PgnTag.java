package org.alcibiade.chess.persistence.pgn;

/**
 * Created by b4dt0bi on 18.07.16.
 */
public class PgnTag implements Comparable<PgnTag> {
    public static String TAG_ID_EVENT = "Event";
    public static String TAG_ID_SITE = "Site";
    public static String TAG_ID_DATE = "Date";
    public static String TAG_ID_ROUND = "Round";
    public static String TAG_ID_WHITE = "White";
    public static String TAG_ID_BLACK = "Black";
    public static String TAG_ID_RESULT = "Result";

    public static String TAG_ID_SETUP = "SetUp";
    public static String TAG_ID_FEN = "FEN";

    public static String[] STR_TAGS = {TAG_ID_EVENT,
            TAG_ID_SITE,
            TAG_ID_DATE,
            TAG_ID_ROUND,
            TAG_ID_WHITE,
            TAG_ID_BLACK,
            TAG_ID_RESULT};

    protected String tag;
    protected String value;

    public PgnTag(final String tag) {
        this.tag = tag;
        this.value = null;
    }

    public PgnTag(final String tag, final String value) {
        this.tag = tag;
        this.value = value;
    }

    @Override
    public String toString() {
        return "[" + tag + " \"" + (value == null ? "?" : value) + "\"]";
    }

    @Override
    public int compareTo(final PgnTag pgnTag) {
        int myStrOrder = getStrOrder();
        int otherStrOrder = pgnTag.getStrOrder();
        if (myStrOrder == -1 && otherStrOrder == -1) {
            // no STR so order it by TAG
            return tag.compareTo(pgnTag.tag);
        }
        if (myStrOrder == -1) {
            return 1;
        } else {
            if (otherStrOrder == -1) {
                return -1;
            } else {
                return myStrOrder < otherStrOrder ? -1 : (myStrOrder == otherStrOrder ? 0 : 1);
            }
        }
    }

    /**
     * Checks if the given PgnTag is part of the STR (Seven Tag Roaster).
     *
     * @return true if it is part of STR
     */
    public boolean isStr() {
        return getStrOrder() != -1;
    }

    public int getStrOrder() {
        for (int i = 0; i < STR_TAGS.length; i++) {
            if (STR_TAGS[i].equals(tag)) {
                return i;
            }
        }
        return -1;
    }

    public String getValue() {
        return value;
    }

    public String getId() {
        return tag;
    }
}
