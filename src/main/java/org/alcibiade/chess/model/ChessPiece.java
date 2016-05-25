package org.alcibiade.chess.model;

import java.io.Serializable;

public class ChessPiece implements Serializable {

    private static final long serialVersionUID = 1;
    private ChessPieceType type;
    private ChessSide side;

    @SuppressWarnings("unused")
    private ChessPiece() {
    }

    public ChessPiece(ChessPieceType type, ChessSide side) {
        this.type = type;
        this.side = side;
    }

    public ChessPieceType getType() {
        return type;
    }

    public ChessSide getSide() {
        return side;
    }

    public String getInitials() {
        return side.getShortName() + type.getShortName();
    }

    /**
     * Get this piece representation as a single character.
     *
     * @return the piece type, with case matching
     * the piece side (uppercase for white, lowercase for black).
     */
    public Character getAsSingleCharacter() {
        Character result = type.getShortName();

        if (side == ChessSide.WHITE) {
            result = Character.toUpperCase(result);
        }

        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof ChessPiece) {
            ChessPiece oPiece = (ChessPiece) obj;
            result = type == oPiece.type && side == oPiece.side;
        }

        return result;
    }

    @Override
    public int hashCode() {
        return type.hashCode() + side.hashCode();
    }

    @Override
    public String toString() {
        return getInitials();
    }
}
