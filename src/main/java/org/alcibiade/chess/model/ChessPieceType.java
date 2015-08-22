package org.alcibiade.chess.model;

public enum ChessPieceType {

    PAWN("p", "pawn"), KNIGHT("n", "knight"), BISHOP("b", "bishop"), ROOK("r", "rook"), QUEEN("q", "queen"), KING("k",
            "king");
    private final String shortName;
    private final String fullName;

    ChessPieceType(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public static ChessPieceType getPgnType(String s) {
        ChessPieceType result = null;

        for (ChessPieceType type : ChessPieceType.values()) {
            if (type.getShortName().equalsIgnoreCase(s)) {
                result = type;
                break;
            }
        }

        return result;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
