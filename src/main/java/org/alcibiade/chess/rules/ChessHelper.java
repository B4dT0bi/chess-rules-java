package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;
import org.alcibiade.chess.persistence.PgnMarshaller;

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

        return isCheck(rules, nextPosition);
    }

    /**
     * Check if we already have a Check situation.
     * @param rules the chess rules component
     * @param position the current position
     * @return true if the position is a check situation
     */
    public static boolean isCheck(ChessRules rules, ChessPosition position) {
        boolean check = false;
        PieceLocator locator = new PieceLocator(position);
        ChessPiece king = new ChessPiece(ChessPieceType.KING, position.getNextPlayerTurn());

        Collection<ChessBoardCoord> kingPositions = locator.locatePiece(king);
        if (!kingPositions.isEmpty()) {
            ChessBoardCoord kingCoords = kingPositions.iterator().next();
            Set<ChessBoardCoord> attackers = rules.getAttackingPieces(position, kingCoords);

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
        return isCheckMate(rules, applyMoveAndSwitch(rules, position, move));
    }

    /**
     * Check if we have a Checkmate situation.
     *
     * @param rules    the chess rules component
     * @param position the current position
     * @return true if the position is a checkmate situation
     * @throws IllegalMoveException
     */
    public static boolean isCheckMate(ChessRules rules, ChessPosition position) {
        return rules.getAvailableMoves(position).isEmpty();
    }

    /**
     * Convenient method to apply a move on a position. Note: this won't update the next player side flag.
     *
     * @param rules    the chess rules component
     * @param position the initial position
     * @param move     the move to apply
     * @return the board position after the move has been played
     * @throws IllegalMoveException
     */
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
     * Convenient method to apply a move on a position and switch the next player flag.
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

    public static ChessPosition movesToPosition(ChessRules chessRules, PgnMarshaller pgnMarshaller, Collection<String> moves) {
        ChessPosition position = chessRules.getInitialPosition();

        for (String move : moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(position, move);
            position = applyMoveAndSwitch(chessRules, position, movePath);
        }

        return position;
    }
}
