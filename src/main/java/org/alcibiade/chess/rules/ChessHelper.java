package org.alcibiade.chess.rules;

import java.util.Collection;
import java.util.Set;
import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPiece;
import org.alcibiade.chess.model.ChessPieceType;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;

public class ChessHelper {

    private ChessHelper() {
    }

    public static boolean isCheck(ChessRules rules, ChessPosition position, ChessMovePath move, boolean swapSides)
            throws IllegalMoveException {
        ChessBoardModel nextPosition = applyMove(rules, position, move);

        if (swapSides) {
            nextPosition.nextPlayerTurn();
        }

        boolean check = false;
        PieceLocator locator = new PieceLocator(nextPosition);
        ChessPiece king = new ChessPiece(ChessPieceType.KING, nextPosition.getNextPlayerTurn());

        Set<ChessBoardCoord> kingPositions = locator.locatePiece(king);
        if (!kingPositions.isEmpty()) {
            ChessBoardCoord kingCoords = kingPositions.iterator().next();
            Set<ChessBoardCoord> attackers = rules.getAttackingPieces(nextPosition, kingCoords);

            check = !attackers.isEmpty();
        }

        return check;
    }

    public static ChessBoardModel applyMove(ChessRules rules, ChessPosition position, ChessMovePath move)
            throws IllegalMoveException {
        ChessBoardModel nextPosition = new ChessBoardModel();
        nextPosition.setPosition(position);

        Collection<ChessBoardUpdate> updates = rules.getUpdatesForMove(position, move);
        for (ChessBoardUpdate update : updates) {
            update.apply(nextPosition);
        }

        return nextPosition;
    }
}
