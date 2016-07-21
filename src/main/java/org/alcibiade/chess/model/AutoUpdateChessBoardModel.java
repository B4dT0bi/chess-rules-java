package org.alcibiade.chess.model;

import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;
import org.alcibiade.chess.persistence.MoveHistoryEntry;
import org.alcibiade.chess.rules.ChessRules;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Tobias Boese <tobias.boese@gmail.com>
 */
public class AutoUpdateChessBoardModel extends ChessBoardModel {
    private List<MoveHistoryEntry> history = new ArrayList<>();
    private ChessRules rules;
    private int position = 0;

    public AutoUpdateChessBoardModel(final ChessRules rules, final String... moves) {
        this.rules = rules;
        if (moves != null) {
            for (String move : moves) {
                update(move);
            }
        }
    }

    public void update(final ChessMovePath move) {
        Collection<ChessBoardUpdate> updates = rules.getUpdatesForMove(this, move);
        ChessBoardModel nextPosition = new ChessBoardModel();
        nextPosition.setPosition(this);
        for (ChessBoardUpdate update : updates) {
            update.apply(nextPosition);
        }
        this.setPosition(nextPosition);
        nextPlayerTurn();
        while (position < history.size()) {
            history.remove(history.size() - 1);
        }
        history.add(new MoveHistoryEntry(move, updates));
        position++;
    }

    public void update(final String move) {
        update(ChessMovePath.fromLAN(move));
    }

    /**
     * Jump to a previous position.
     */
    public void prev() {
        if (position > 0) {
            position--;
            MoveHistoryEntry moveHistoryEntry = history.get(position);
            ChessBoardModel nextPosition = new ChessBoardModel();
            nextPosition.setPosition(this);
            for (ChessBoardUpdate update : moveHistoryEntry.getReverts()) {
                update.revert(nextPosition);
            }
            this.setPosition(nextPosition);
            nextPlayerTurn();
        }
    }

    public void first() {
        while (position > 0) {
            prev();
        }
    }

    public void last() {
        while (hasNext()) {
            next();
        }
    }

    public void next() {
        if (hasNext()) {
            MoveHistoryEntry moveHistoryEntry = history.get(position);
            ChessBoardModel nextPosition = new ChessBoardModel();
            nextPosition.setPosition(this);
            for (ChessBoardUpdate update : moveHistoryEntry.getUpdates()) {
                update.apply(nextPosition);
            }
            this.setPosition(nextPosition);
            nextPlayerTurn();
            position++;
        }
    }

    public boolean hasNext() {
        return position < history.size();
    }

    public Collection<String> getMoves() {
        List<String> moves = new ArrayList<String>();
        for (int i = 0; i < position; i++) {
            moves.add(history.get(i).getMove().toLanString());
        }
        return moves;
    }

    public Collection<MoveHistoryEntry> getMoveHistoryEntries() {
        return history;
    }

    public ChessRules getRules() {
        return rules;
    }
}
