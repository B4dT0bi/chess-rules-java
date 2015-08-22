package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;

import java.util.Collection;
import java.util.Set;

public class ChessHelper {

    private ChessHelper() {
    }

    /**
     * Check if a move would lead to a Check situation.
     *
     * @param rules the chess rules component
     * @param position the initial position
     * @param move the move to check
     * @param swapSides if true check if the move would lead to a check in favor of the player moving, if false for the
     * opponent.
     * @return
     * @throws IllegalMoveException
     */
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

    public static ChessBoardModel applyMoveAndSwitch(
            ChessRules rules, ChessPosition position, ChessMovePath move)
            throws IllegalMoveException {
        ChessBoardModel model = applyMove(rules, position, move);
        model.nextPlayerTurn();
        return model;
    }

}
