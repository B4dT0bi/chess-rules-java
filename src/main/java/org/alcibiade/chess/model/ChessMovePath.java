package org.alcibiade.chess.model;

public class ChessMovePath extends ChessBoardPath {

    private static final long serialVersionUID = 1;
    private ChessPieceType promotedPieceType = ChessPieceType.QUEEN;

    @SuppressWarnings("unused")
    private ChessMovePath() {
    }

    public ChessMovePath(String lanMove) {
        this(lanMove.substring(0, 2), lanMove.substring(2, 4), lanMove.length() > 4 ? lanMove.substring(4, 5) : null);
    }

    public ChessMovePath(ChessBoardCoord s, ChessBoardCoord d) {
        super(s, d);
    }

    public ChessMovePath(String s, String d) {
        super(s, d);
    }

    public ChessMovePath(String s, String d, String piece) {
        super(s, d);
        promotedPieceType = piece == null ? null : ChessPieceType.getPgnType(piece);
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ChessMovePath)) return false;
        if (!super.equals(o)) return false;

        ChessMovePath that = (ChessMovePath) o;

        return promotedPieceType == that.promotedPieceType;

    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (promotedPieceType != null ? promotedPieceType.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "ChessMovePath<" + getSource().getPgnCoordinates() + ":" + getDestination().getPgnCoordinates() + "="
                + promotedPieceType + ">";
    }

    /**
     * Convert the ChessMovePath to Long Algebraic Notation.
     *
     * @return
     */
    public String toLanString() {
        return getSource().getPgnCoordinates() + getDestination().getPgnCoordinates() + (promotedPieceType == null ? "" : promotedPieceType.getShortName());
    }

    /**
     * Create a ChessMovePath from a Long Algebraic Notation.
     *
     * @param move the move in long algebraic notation
     * @return
     */
    public static ChessMovePath fromLAN(final String move) {
        ChessBoardCoord src = new ChessBoardCoord(move.substring(0, 2));
        ChessBoardCoord dst = new ChessBoardCoord(move.substring(2, 4));
        if (move.length() == 5) {
            return new ChessMovePath(src, dst, ChessPieceType.getPgnType(move.substring(4)));
        } else {
            return new ChessMovePath(src, dst, null);
        }
    }
}
