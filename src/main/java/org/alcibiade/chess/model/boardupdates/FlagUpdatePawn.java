package org.alcibiade.chess.model.boardupdates;

import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessBoardModel;

public class FlagUpdatePawn extends AbstractBoardUpdate {

    private static final long serialVersionUID = 1;
    private ChessBoardCoord pawnCoord;
    private ChessBoardCoord backup;

    @SuppressWarnings("unused")
    private FlagUpdatePawn() {
    }

    public FlagUpdatePawn(ChessBoardCoord coord) {
        this.pawnCoord = coord;
        this.backup = null;
    }

    @Override
    public void apply(ChessBoardModel boardModel) {
        backup = boardModel.getLastPawnDMove();
        boardModel.setLastPawnDMove(pawnCoord);
    }

    @Override
    public void revert(ChessBoardModel boardModel) {
        boardModel.setLastPawnDMove(backup);
    }

    @Override
    public String toString() {
        return "FlagUpdatePawn " + (pawnCoord == null ? "null" : pawnCoord.getPgnCoordinates());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FlagUpdatePawn)) return false;

        FlagUpdatePawn that = (FlagUpdatePawn) o;

        if (pawnCoord != null ? !pawnCoord.equals(that.pawnCoord) : that.pawnCoord != null) return false;
        return backup != null ? backup.equals(that.backup) : that.backup == null;

    }

    @Override
    public int hashCode() {
        int result = pawnCoord != null ? pawnCoord.hashCode() : 0;
        result = 31 * result + (backup != null ? backup.hashCode() : 0);
        return result;
    }
}
