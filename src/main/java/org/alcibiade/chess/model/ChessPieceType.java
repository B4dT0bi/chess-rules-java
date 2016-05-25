package org.alcibiade.chess.model;

public enum ChessPieceType {

    PAWN('p', "pawn"), KNIGHT('n', "knight"), BISHOP('b', "bishop"),
    ROOK('r', "rook"), QUEEN('q', "queen"), KING('k', "king");

    private final Character shortName;
    private final String fullName;

    ChessPieceType(Character shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public static ChessPieceType getPgnType(String s) {
        ChessPieceType result = null;

        for (ChessPieceType type : ChessPieceType.values()) {
            if (type.getShortName() == s.toLowerCase().charAt(0)) {
                result = type;
                break;
            }
        }

        return result;
    }

    public Character getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
