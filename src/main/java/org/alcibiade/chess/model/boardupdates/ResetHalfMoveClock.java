package org.alcibiade.chess.model.boardupdates;

import org.alcibiade.chess.model.ChessBoardModel;

public class ResetHalfMoveClock extends AbstractBoardUpdate {

    private static final long serialVersionUID = 1;
    private int halfMoveClockBackup;


    public void apply(ChessBoardModel boardModel) {
        halfMoveClockBackup = boardModel.getHalfMoveClock();
        boardModel.setHalfMoveClock(0);
    }

    public void revert(ChessBoardModel boardModel) {
        boardModel.setHalfMoveClock(halfMoveClockBackup);
    }

    @Override
    public String toString() {
        return "Reset half move clock";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ResetHalfMoveClock)) return false;

        ResetHalfMoveClock that = (ResetHalfMoveClock) o;

        return halfMoveClockBackup == that.halfMoveClockBackup;

    }

    @Override
    public int hashCode() {
        return halfMoveClockBackup;
    }
}
