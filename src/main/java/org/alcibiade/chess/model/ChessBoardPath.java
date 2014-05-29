package org.alcibiade.chess.model;

import java.io.Serializable;

public class ChessBoardPath implements Serializable {

    private static final long serialVersionUID = 1;
    private ChessBoardCoord source;
    private ChessBoardCoord destination;

    protected ChessBoardPath() {
    }

    public ChessBoardPath(String source, String destination) {
        assert source != null && destination != null;
        this.source = new ChessBoardCoord(source);
        this.destination = new ChessBoardCoord(destination);
    }

    public ChessBoardPath(ChessBoardCoord source, ChessBoardCoord destination) {
        assert source != null && destination != null;
        this.source = source;
        this.destination = destination;
    }

    public ChessBoardCoord getSource() {
        return source;
    }

    public ChessBoardCoord getDestination() {
        return destination;
    }

    public int get4Distance() {
        return Math.abs(source.getRow() - destination.getRow()) + Math.abs(source.getCol() - destination.getCol());
    }

    public int get8Distance() {
        int dx = Math.abs(source.getCol() - destination.getCol());
        int dy = Math.abs(source.getRow() - destination.getRow());
        return Math.max(dx, dy);
    }

    public boolean isOverlapping(ChessBoardCoord c) {
        boolean result = false;

        boolean inArea = (isInInterval(source.getRow(), c.getRow(), destination.getRow()))
                && (isInInterval(source.getCol(), c.getCol(), destination.getCol()));

        if (inArea) {
            int dx = Math.abs(destination.getCol() - source.getCol());
            int dy = Math.abs(destination.getRow() - source.getRow());
            if (dx == 0 || dy == 0) {
                // Horixontal or vertical line, all the area is valid.
                result = true;
            } else if (dx == dy) {
                // Diagonal
                result = Math.abs(source.getRow() - c.getRow()) == Math.abs(source.getCol() - c.getCol());
            } else {
                // L (Angle)
                if (dx > dy) {
                    result = c.getRow() == source.getRow() || c.getCol() == destination.getCol();
                } else {
                    result = c.getRow() == destination.getRow() || c.getCol() == source.getCol();
                }
            }
        }

        return result;
    }

    private static boolean isInInterval(int min, int x, int max) {
        return Math.min(min, max) <= x && x <= Math.max(min, max);
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof ChessBoardPath) {
            ChessBoardPath otherPath = (ChessBoardPath) obj;
            result = source.equals(otherPath.source) && destination.equals(otherPath.destination);
        }

        return result;
    }

    @Override
    public int hashCode() {
        return source.hashCode() + 11 * destination.hashCode();
    }

    @Override
    public String toString() {
        return "Path{" + source.getPgnCoordinates() + ":" + destination.getPgnCoordinates() + "}";
    }
}
