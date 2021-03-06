package org.alcibiade.chess.model;

import java.io.Serializable;
import java.util.*;

public class ChessBoardCoord implements Comparable<ChessBoardCoord>, Serializable {

    private static final String[] COLNAMES = {"a", "b", "c", "d", "e", "f", "g", "h"};
    private static final long serialVersionUID = 1;
    private static SortedSet<ChessBoardCoord> boardCoords = null;

    static {
        SortedSet<ChessBoardCoord> coords = new TreeSet<>();

        for (int i = 0; i < 64; i++) {
            coords.add(new ChessBoardCoord(i));
        }

        ChessBoardCoord.boardCoords = Collections.unmodifiableSortedSet(coords);
    }

    private int offset;

    public ChessBoardCoord() {
    }

    public ChessBoardCoord(String pgnCoord) {
        String colName = pgnCoord.substring(0, 1).toLowerCase();
        String rowName = pgnCoord.substring(1, 2);

        int col = getColFromName(colName);
        int row = getRowFromName(rowName);
        assert row >= 0;
        assert row < 8;
        assert col >= 0;
        assert col < 8;
        this.offset = col + row * 8;
    }

    public ChessBoardCoord(int col, int row) {
        this(col + row * 8);
        assert row >= 0;
        assert row < 8;
        assert col >= 0;
        assert col < 8;
    }

    public ChessBoardCoord(int offset) {
        assert offset >= 0;
        assert offset < 64;
        this.offset = offset;
    }

    public static int getRowFromName(String rowName) {
        int row = Integer.parseInt(rowName) - 1;
        assert row >= 0;
        assert row < 8;
        return row;
    }

    public static int getColFromName(String colName) {
        List<String> colNamesList = Arrays.asList(COLNAMES);
        int col = colNamesList.indexOf(colName);
        assert col >= 0;
        assert col < 8;
        return col;
    }

    public int getOffset() {
        return offset;
    }

    public int getRow() {
        return offset / 8;
    }

    public int getCol() {
        return offset % 8;
    }

    public String getPgnCoordinates() {
        return COLNAMES[getCol()] + (getRow() + 1);
    }

    public ChessBoardCoord add(ChessBoardCoord coord) {
        return add(coord.getCol(), coord.getRow());
    }

    public ChessBoardCoord add(int x, int y) {
        return new ChessBoardCoord(getCol() + x, getRow() + y);
    }

    public static SortedSet<ChessBoardCoord> getAllBoardCoords() {
        return boardCoords;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof ChessBoardCoord) {
            ChessBoardCoord otherCoord = (ChessBoardCoord) obj;
            result = offset == otherCoord.offset;
        }

        return result;
    }

    @Override
    public int hashCode() {
        return offset;
    }

    @Override
    public String toString() {
        return "BoardCoord{" + getPgnCoordinates() + ", offs=" + offset + "}";
    }

    @Override
    public int compareTo(ChessBoardCoord o) {
        return this.offset - o.offset;
    }
}
