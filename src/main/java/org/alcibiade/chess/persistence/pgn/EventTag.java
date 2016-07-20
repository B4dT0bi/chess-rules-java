package org.alcibiade.chess.persistence.pgn;

/**
 * Created by b4dt0bi on 19.07.16.
 */
public class EventTag extends PgnTag {
    public EventTag() {
        super(TAG_ID_EVENT);
    }

    public EventTag(final String event) {
        super(TAG_ID_EVENT);
        setEvent(event);
    }

    public void setEvent(final String event) {
        value = (event == null || event.isEmpty()) ? "?" : event;
    }
}
