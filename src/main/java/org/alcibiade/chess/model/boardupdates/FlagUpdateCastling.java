package org.alcibiade.chess.model.boardupdates;

import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessSide;

public class FlagUpdateCastling extends AbstractBoardUpdate {

    private static final long serialVersionUID = 1;
    private ChessSide side;
    private boolean kingside;
    private boolean backup;

    @SuppressWarnings("unused")
    private FlagUpdateCastling() {
    }

    public FlagUpdateCastling(ChessSide side, boolean kingside) {
        this.side = side;
        this.kingside = kingside;
    }

    public void apply(ChessBoardModel boardModel) {
        backup = boardModel.isCastlingAvailable(side, kingside);
        boardModel.setCastlingAvailable(side, kingside, false);
    }

    public void revert(ChessBoardModel boardModel) {
        boardModel.setCastlingAvailable(side, kingside, backup);
    }

    @Override
    public String toString() {
        return "FlagUpdateCastling for " + side + " kingside=" + kingside;
    }
}
