package org.alcibiade.chess.model.boardupdates;

import org.alcibiade.chess.model.ChessBoardModel;

public interface ChessBoardUpdate {

    void apply(ChessBoardModel boardModel);

    void revert(ChessBoardModel boardModel);
}
