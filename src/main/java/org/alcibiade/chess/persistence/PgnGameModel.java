package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.persistence.pgn.*;

import java.util.*;

/**
 * Model mapped to a typical PGN game file.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
public class PgnGameModel {

    private Map<String, PgnTag> tagMap = new HashMap<>();
    private List<String> moves;
    private ChessPosition position;

    public PgnGameModel() {

    }

    public PgnGameModel(
            String whitePlayerName, String blackPlayerName, Date gameDate, String result,
            String event, String site, String round,
            List<String> moves) {
        addTag(new WhiteTag(whitePlayerName));
        addTag(new BlackTag(blackPlayerName));
        addTag(new DateTag(gameDate));
        this.moves = moves;
        addTag(new ResultTag(result));
        addTag(new EventTag(event));
        addTag(new SiteTag(site));
        addTag(new RoundTag(round));
    }

    public List<String> getMoves() {
        return Collections.unmodifiableList(moves);
    }

    public String getWhitePlayerName() {
        return getTagValue(PgnTag.TAG_ID_WHITE);
    }

    public String getBlackPlayerName() {
        return getTagValue(PgnTag.TAG_ID_BLACK);
    }

    public Date getGameDate() {
        return tagMap.get(PgnTag.TAG_ID_DATE) == null ? null : ((DateTag) tagMap.get(PgnTag.TAG_ID_DATE)).getDate();
    }

    public String getResult() {
        return getTagValue(PgnTag.TAG_ID_RESULT);
    }

    public String getEvent() {
        return getTagValue(PgnTag.TAG_ID_EVENT);
    }

    public String getSite() {
        return getTagValue(PgnTag.TAG_ID_SITE);
    }

    public String getRound() {
        return getTagValue(PgnTag.TAG_ID_ROUND);
    }

    public String getTagValue(String id) {
        if (tagMap.containsKey(id)) {
            return tagMap.get(id).getValue();
        }
        return null;
    }

    public Collection<PgnTag> getTags() {
        return tagMap.values();
    }

    public void addTag(PgnTag tag) {
        tagMap.put(tag.getId(), tag);
    }

    public ChessPosition getPosition() {
        return position;
    }

    public void setPosition(ChessPosition position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return String.format("%s vs. %s (%d moves, result: %s)", getWhitePlayerName(), getBlackPlayerName(), moves.size(), getResult());
    }

    public void setMoves(List<String> moves) {
        this.moves = moves;
    }
}
