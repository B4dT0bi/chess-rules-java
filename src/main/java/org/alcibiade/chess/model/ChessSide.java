package org.alcibiade.chess.model;

public enum ChessSide {

    WHITE("w", "white"), BLACK("b", "black");
    private final String shortName;
    private final String fullName;

    ChessSide(String shortName, String fullName) {
        this.shortName = shortName;
        this.fullName = fullName;
    }

    public String getShortName() {
        return shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public ChessSide opposite() {
        return this == WHITE ? BLACK : WHITE;
    }
}
