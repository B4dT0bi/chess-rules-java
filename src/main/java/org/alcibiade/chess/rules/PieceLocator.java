package org.alcibiade.chess.rules;

import java.util.HashSet;
import java.util.Set;
import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessPiece;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.ChessSide;
import org.apache.commons.lang.ObjectUtils;

public class PieceLocator {

    private ChessPosition position;

    public PieceLocator(ChessPosition position) {
        this.position = position;
    }

    public Set<ChessBoardCoord> locatePiece(ChessPiece piece) {
        Set<ChessBoardCoord> coords = new HashSet<ChessBoardCoord>();

        for (ChessBoardCoord coord : ChessBoardCoord.getAllBoardCoords()) {
            ChessPiece localPiece = position.getPiece(coord);
            if (ObjectUtils.equals(piece, localPiece)) {
                coords.add(coord);
            }
        }

        return coords;
    }

    public Set<ChessBoardCoord> locatePieces(ChessSide side) {
        Set<ChessBoardCoord> coords = new HashSet<ChessBoardCoord>();

        for (ChessBoardCoord coord : ChessBoardCoord.getAllBoardCoords()) {
            ChessPiece localPiece = position.getPiece(coord);
            if (localPiece != null && localPiece.getSide() == side) {
                coords.add(coord);
            }
        }

        return coords;
    }
}
