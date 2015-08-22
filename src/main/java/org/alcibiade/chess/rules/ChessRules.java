package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;

import java.util.List;
import java.util.Set;

public interface ChessRules {
    Set<ChessBoardCoord> getReachableDestinations(ChessPosition position, ChessBoardCoord pieceCoords,
            boolean excludeCheckSituations);

    Set<ChessBoardCoord> getAttackingPieces(ChessPosition position, ChessBoardCoord square);

    ChessPosition getInitialPosition();

    ChessGameStatus getStatus(ChessPosition position);

    Set<ChessMovePath> getAvailableMoves(ChessPosition position);

    List<ChessBoardUpdate> getUpdatesForMove(ChessPosition position, ChessMovePath path) throws IllegalMoveException;
}
