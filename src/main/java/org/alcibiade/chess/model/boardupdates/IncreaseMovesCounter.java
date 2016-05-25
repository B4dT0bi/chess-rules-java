package org.alcibiade.chess.model.boardupdates;

import org.alcibiade.chess.model.ChessBoardModel;

public class IncreaseMovesCounter extends AbstractBoardUpdate {

    private static final long serialVersionUID = 1;


    public void apply(ChessBoardModel boardModel) {
        boardModel.setMoveNumber(boardModel.getMoveNumber() + 1);
    }

    public void revert(ChessBoardModel boardModel) {
        boardModel.setMoveNumber(boardModel.getMoveNumber() - 1);
    }

    @Override
    public String toString() {
        return "Increase move counter";
    }
}
