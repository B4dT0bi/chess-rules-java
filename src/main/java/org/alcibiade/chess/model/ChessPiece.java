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
