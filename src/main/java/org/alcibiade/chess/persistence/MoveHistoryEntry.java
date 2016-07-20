package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Created by b4dt0bi on 13.07.16.
 */
public class MoveHistoryEntry {
    private ChessMovePath move;
    private Collection<ChessBoardUpdate> updates;

    public MoveHistoryEntry(ChessMovePath move, Collection<ChessBoardUpdate> updates) {
        this.move = move;
        this.updates = updates;
    }

    public ChessMovePath getMove() {
        return move;
    }

    public Collection<ChessBoardUpdate> getUpdates() {
        return updates;
    }

    public Collection<ChessBoardUpdate> getReverts() {
        List<ChessBoardUpdate> rev = new ArrayList<>(updates);
        Collections.reverse(rev);
        return rev;
    }
}
