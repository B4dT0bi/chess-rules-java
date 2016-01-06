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
     * @param rules     the chess rules component
     * @param position  the initial position
     * @param move      the move to check
     * @param swapSides if true check if the move would lead to a check in favor of the player moving, if false for the
     *                  opponent.
     * @return true if the position is a check situation
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

        Collection<ChessBoardCoord> kingPositions = locator.locatePiece(king);
        if (!kingPositions.isEmpty()) {
            ChessBoardCoord kingCoords = kingPositions.iterator().next();
            Set<ChessBoardCoord> attackers = rules.getAttackingPieces(nextPosition, kingCoords);

            check = !attackers.isEmpty();
        }

        return check;
    }

    /**
     * Check if a move would lead to a Checkmate situation.
     *
     * @param rules    the chess rules component
     * @param position the initial position
     * @param move     the move to check
     * @return true if the position is a check situation
     * @throws IllegalMoveException
     */
    public static boolean isCheckMate(ChessRules rules, ChessPosition position, ChessMovePath move) {
        ChessPosition targetPosition = applyMoveAndSwitch(rules, position, move);
        return rules.getAvailableMoves(targetPosition).isEmpty();
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

    /**
     * Convenient method to apply a move on a position.
     *
     * @param rules    the chess rules component
     * @param position the initial position
     * @param move     the move to apply
     * @return the board position after the move has been played
     * @throws IllegalMoveException
     */
    public static ChessBoardModel applyMoveAndSwitch(
            ChessRules rules, ChessPosition position, ChessMovePath move)
            throws IllegalMoveException {
        ChessBoardModel model = applyMove(rules, position, move);
        model.nextPlayerTurn();
        return model;
    }

}
