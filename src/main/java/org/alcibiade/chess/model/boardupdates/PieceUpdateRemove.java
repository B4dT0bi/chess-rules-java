package org.alcibiade.chess.model.boardupdates;

import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessPiece;

public class PieceUpdateRemove extends AbstractBoardUpdate {

    private static final long serialVersionUID = 1;
    private ChessBoardCoord coordinates;
    private ChessPiece removedPiece;

    @SuppressWarnings("unused")
    private PieceUpdateRemove() {
    }

    public PieceUpdateRemove(ChessBoardCoord coordinates) {
        assert coordinates != null;
        this.coordinates = coordinates;
    }

    public ChessBoardCoord getCoordinates() {
        return coordinates;
    }

    @Override
    public void apply(ChessBoardModel boardModel) {
        removedPiece = boardModel.getPiece(coordinates);
        assert removedPiece != null;
        boardModel.setPiece(coordinates, null);
    }

    @Override
    public void revert(ChessBoardModel boardModel) {
        assert boardModel.getPiece(coordinates) == null;
        assert removedPiece != null;
        boardModel.setPiece(coordinates, removedPiece);
    }

    @Override
    public String toString() {
        return "PieceUpdateRemove at " + coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PieceUpdateRemove)) return false;

        PieceUpdateRemove that = (PieceUpdateRemove) o;

        if (coordinates != null ? !coordinates.equals(that.coordinates) : that.coordinates != null) return false;
        return removedPiece != null ? removedPiece.equals(that.removedPiece) : that.removedPiece == null;

    }

    @Override
    public int hashCode() {
        int result = coordinates != null ? coordinates.hashCode() : 0;
        result = 31 * result + (removedPiece != null ? removedPiece.hashCode() : 0);
        return result;
    }
}
