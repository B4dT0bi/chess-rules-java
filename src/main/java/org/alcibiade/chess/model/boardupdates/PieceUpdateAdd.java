package org.alcibiade.chess.model.boardupdates;

import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessPiece;

public class PieceUpdateAdd extends AbstractBoardUpdate {

    private static final long serialVersionUID = 1;
    private ChessBoardCoord coordinates;
    private ChessPiece piece;

    @SuppressWarnings("unused")
    private PieceUpdateAdd() {
    }

    public PieceUpdateAdd(ChessBoardCoord coordinates, ChessPiece piece) {
        assert coordinates != null;
        assert piece != null;
        this.coordinates = coordinates;
        this.piece = piece;
    }

    public ChessBoardCoord getCoordinates() {
        return coordinates;
    }

    public ChessPiece getPiece() {
        return piece;
    }

    @Override
    public void apply(ChessBoardModel boardModel) {
        assert boardModel.getPiece(coordinates) == null;
        boardModel.setPiece(coordinates, piece);
    }

    @Override
    public void revert(ChessBoardModel boardModel) {
        assert boardModel.getPiece(coordinates) == piece;
        boardModel.setPiece(coordinates, null);
    }

    @Override
    public String toString() {
        return "PieceUpdateAdd " + piece + " at " + coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PieceUpdateAdd)) return false;

        PieceUpdateAdd that = (PieceUpdateAdd) o;

        if (coordinates != null ? !coordinates.equals(that.coordinates) : that.coordinates != null) return false;
        return piece != null ? piece.equals(that.piece) : that.piece == null;

    }

    @Override
    public int hashCode() {
        int result = coordinates != null ? coordinates.hashCode() : 0;
        result = 31 * result + (piece != null ? piece.hashCode() : 0);
        return result;
    }
}
