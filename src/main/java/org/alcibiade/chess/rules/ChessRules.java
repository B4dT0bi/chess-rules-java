package org.alcibiade.chess.rules;

import java.util.List;
import java.util.Set;
import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessGameStatus;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;

public interface ChessRules {
    Set<ChessBoardCoord> getReachableDestinations(ChessPosition position, ChessBoardCoord pieceCoords,
            boolean excludeCheckSituations);

    Set<ChessBoardCoord> getAttackingPieces(ChessPosition position, ChessBoardCoord square);

    ChessPosition getInitialPosition();

    ChessGameStatus getStatus(ChessPosition position);

    Set<ChessMovePath> getAvailableMoves(ChessPosition position);

    List<ChessBoardUpdate> getUpdatesForMove(ChessPosition position, ChessMovePath path) throws IllegalMoveException;
}
