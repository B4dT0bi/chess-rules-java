package org.alcibiade.chess.model;

public class ChessMovePath extends ChessBoardPath {

    private static final long serialVersionUID = 1;
    private ChessPieceType promotedPieceType = ChessPieceType.QUEEN;

    @SuppressWarnings("unused")
    private ChessMovePath() {
    }

    public ChessMovePath(ChessBoardCoord s, ChessBoardCoord d) {
        super(s, d);
    }

    public ChessMovePath(String s, String d) {
        super(s, d);
    }

    public ChessMovePath(ChessBoardCoord s, ChessBoardCoord d, ChessPieceType promotedPieceType) {
        super(s, d);
        this.promotedPieceType = promotedPieceType;
    }

    public ChessMovePath(String s, String d, ChessPieceType promotedPieceType) {
        super(s, d);
        this.promotedPieceType = promotedPieceType;
    }

    public ChessPieceType getPromotedPieceType() {
        return promotedPieceType;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof ChessMovePath) {
            ChessMovePath oPath = (ChessMovePath) obj;
            result = super.equals(obj) && promotedPieceType == oPath.promotedPieceType;
        }

        return result;
    }

    @Override
    public int hashCode() {
        return super.hashCode() + promotedPieceType.hashCode();
    }

    @Override
    public String toString() {
        return "ChessMovePath<" + getSource().getPgnCoordinates() + ":" + getDestination().getPgnCoordinates() + "="
                + promotedPieceType + ">";
    }
}
