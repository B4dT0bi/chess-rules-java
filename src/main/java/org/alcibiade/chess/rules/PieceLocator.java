package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessPiece;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.ChessSide;
import org.apache.commons.lang.ObjectUtils;

import java.util.HashSet;
import java.util.Set;

/**
 * Optimized piece location helper.
 */
public class PieceLocator {
    private static final ChessBoardCoord[] BOARD_COORDINATES;

    static {
        BOARD_COORDINATES = ChessBoardCoord.getAllBoardCoords().toArray(new ChessBoardCoord[64]);
    }

    private ChessPosition position;

    public PieceLocator(ChessPosition position) {
        this.position = position;
    }

    /**
     * Locate a piece on the board. Since we often have several similar pieces
     * on the board, the result is a set of coordinates.
     *
     * @param piece a piece that should be located on the board
     * @return the position of every piece occurrence. May be empty if the
     * piece is no longer present on the board.
     */
    public Set<ChessBoardCoord> locatePiece(ChessPiece piece) {
        Set<ChessBoardCoord> coords = new HashSet<>();

        for (ChessBoardCoord coord : BOARD_COORDINATES) {
            ChessPiece localPiece = position.getPiece(coord);
            if (ObjectUtils.equals(piece, localPiece)) {
                coords.add(coord);
            }
        }

        return coords;
    }

    /**
     * Locate coordinates of all pieces of a given playing side.
     *
     * @param side the side whose pieces should be located on the board
     * @return the position of every piece occurrence. Should never be empty
     * if the game position is valid.
     */
    public Set<ChessBoardCoord> locatePieces(ChessSide side) {
        Set<ChessBoardCoord> coords = new HashSet<>();

        for (ChessBoardCoord coord : BOARD_COORDINATES) {
            ChessPiece localPiece = position.getPiece(coord);
            if (localPiece != null && localPiece.getSide() == side) {
                coords.add(coord);
            }
        }

        return coords;
    }
}
