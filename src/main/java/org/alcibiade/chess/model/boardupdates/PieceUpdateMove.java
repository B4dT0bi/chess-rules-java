package org.alcibiade.chess.model.boardupdates;

import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessBoardPath;

public class PieceUpdateMove extends AbstractBoardUpdate {

    private static final long serialVersionUID = 1;
    private ChessBoardPath path;

    @SuppressWarnings("unused")
    private PieceUpdateMove() {
    }

    public PieceUpdateMove(ChessBoardPath path) {
        assert path != null;
        this.path = path;
    }

    public ChessBoardPath getPath() {
        return path;
    }

    @Override
    public void apply(ChessBoardModel boardModel) {
        assert boardModel.getPiece(path.getSource()) != null;
        assert boardModel.getPiece(path.getDestination()) == null;
        boardModel.setPiece(path.getDestination(), boardModel.getPiece(path.getSource()));
        boardModel.setPiece(path.getSource(), null);
    }

    @Override
    public void revert(ChessBoardModel boardModel) {
        assert boardModel.getPiece(path.getSource()) == null;
        assert boardModel.getPiece(path.getDestination()) != null;
        boardModel.setPiece(path.getSource(), boardModel.getPiece(path.getDestination()));
        boardModel.setPiece(path.getDestination(), null);
    }

    @Override
    public String toString() {
        return "PieceUpdateMove with path " + path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PieceUpdateMove)) return false;

        PieceUpdateMove that = (PieceUpdateMove) o;

        return path != null ? path.equals(that.path) : that.path == null;

    }

    @Override
    public int hashCode() {
        return path != null ? path.hashCode() : 0;
    }
}
