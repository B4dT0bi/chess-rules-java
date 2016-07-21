package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

public class PieceMoveManager {

    private ChessPosition position;

    public PieceMoveManager(ChessPosition position) {
        this.position = position;
    }

    // SUPPRESS CHECKSTYLE NPath
    public Set<ChessBoardCoord> getReachableSquares(ChessBoardCoord coord, ChessRules rules) {
        Set<ChessBoardCoord> reachable = new TreeSet<>();
        ChessPiece piece = position.getPiece(coord);

        if (piece != null) {
            ChessSide player = piece.getSide();
            int dy = player == ChessSide.WHITE ? 1 : -1;
            int baseRow = player == ChessSide.WHITE ? 0 : 7;

            switch (piece.getType()) {
                case PAWN:
                    handlePawnMove(reachable, coord, dy, baseRow, player);
                    break;

                case KNIGHT:
                    handleKnightMove(reachable, player, coord);
                    break;

                case KING:
                    handleKingMove(reachable, player, coord, baseRow, rules);
                    break;

                case ROOK:
                    handleRookMove(reachable, player, coord);
                    break;

                case BISHOP:
                    handleBishopMove(reachable, player, coord);
                    break;

                case QUEEN:
                    handleRookMove(reachable, player, coord);
                    handleBishopMove(reachable, player, coord);
                    break;
            }
        }
        // check for Check situations
        if (rules != null) {
            ChessBoardCoord kingCoords = null;
            for (ChessBoardCoord boardCoord : ChessBoardCoord.getAllBoardCoords()) {
                ChessPiece cp = position.getPiece(boardCoord);
                if (cp != null && cp.getType() == ChessPieceType.KING && cp.getSide() == position.getNextPlayerTurn()) {
                    kingCoords = boardCoord;
                }
            }
            if (kingCoords == null) {
                return reachable; // FIXME : something went wrong here (no king found?) but for now just return the data (maybe this doesnt happen at all)
            }
            Iterator<ChessBoardCoord> iterator = reachable.iterator();
            while (iterator.hasNext()) {
                ChessBoardCoord reachableDest = iterator.next();
                ChessPosition pos2 = ChessHelper.applyMove(rules, position, new ChessMovePath(coord, reachableDest));
                if (!rules.getAttackingPieces(pos2, kingCoords.equals(coord) ? reachableDest : kingCoords).isEmpty()) {
                    iterator.remove();
                }
            }
        }
        return reachable;
    }

    private void handleBishopMove(Set<ChessBoardCoord> reachable, ChessSide player, ChessBoardCoord coord) {
        isOpponentOrFreeRecursive(reachable, player, coord, +1, +1);
        isOpponentOrFreeRecursive(reachable, player, coord, +1, -1);
        isOpponentOrFreeRecursive(reachable, player, coord, -1, +1);
        isOpponentOrFreeRecursive(reachable, player, coord, -1, -1);
    }

    private void handleRookMove(Set<ChessBoardCoord> reachable, ChessSide player, ChessBoardCoord coord) {
        isOpponentOrFreeRecursive(reachable, player, coord, +1, 0);
        isOpponentOrFreeRecursive(reachable, player, coord, -1, 0);
        isOpponentOrFreeRecursive(reachable, player, coord, 0, +1);
        isOpponentOrFreeRecursive(reachable, player, coord, 0, -1);
    }

    private void handleKingMove(Set<ChessBoardCoord> reachable, ChessSide player, ChessBoardCoord coord, int baseRow, ChessRules rules) {
        add(reachable, isOpponentOrFree(player, coord, +1, +1));
        add(reachable, isOpponentOrFree(player, coord, +0, +1));
        add(reachable, isOpponentOrFree(player, coord, -1, +1));
        add(reachable, isOpponentOrFree(player, coord, +1, +0));
        add(reachable, isOpponentOrFree(player, coord, -1, +0));
        add(reachable, isOpponentOrFree(player, coord, +1, -1));
        add(reachable, isOpponentOrFree(player, coord, +0, -1));
        add(reachable, isOpponentOrFree(player, coord, -1, -1));

        if (position.isCastlingAvailable(player, true)) {
            boolean positionOk = position.getPiece(new ChessBoardCoord(5, baseRow))
                    == null;
            positionOk = positionOk && position.getPiece(new ChessBoardCoord(6, baseRow))
                    == null;

            if (rules != null) {
                positionOk = positionOk
                        && rules.getAttackingPieces(position, new ChessBoardCoord(4,
                        baseRow)).isEmpty();
                positionOk = positionOk
                        && rules.getAttackingPieces(position, new ChessBoardCoord(5,
                        baseRow)).isEmpty();
                positionOk = positionOk
                        && rules.getAttackingPieces(position, new ChessBoardCoord(6,
                        baseRow)).isEmpty();
            }

            if (positionOk) {
                add(reachable, player == ChessSide.WHITE ? Castling.CASTLEWHITEK.
                        getDestination()
                        : Castling.CASTLEBLACKK.getDestination());
            }
        }

        if (position.isCastlingAvailable(player, false)) {
            boolean positionOk = position.getPiece(new ChessBoardCoord(1, baseRow))
                    == null;
            positionOk = positionOk && position.getPiece(new ChessBoardCoord(2, baseRow))
                    == null;
            positionOk = positionOk && position.getPiece(new ChessBoardCoord(3, baseRow))
                    == null;

            if (rules != null) {
                positionOk = positionOk
                        && rules.getAttackingPieces(position, new ChessBoardCoord(1,
                        baseRow)).isEmpty();
                positionOk = positionOk
                        && rules.getAttackingPieces(position, new ChessBoardCoord(2,
                        baseRow)).isEmpty();
                positionOk = positionOk
                        && rules.getAttackingPieces(position, new ChessBoardCoord(3,
                        baseRow)).isEmpty();
                positionOk = positionOk
                        && rules.getAttackingPieces(position, new ChessBoardCoord(4,
                        baseRow)).isEmpty();
            }

            if (positionOk) {
                add(reachable, player == ChessSide.WHITE ? Castling.CASTLEWHITEQ.
                        getDestination()
                        : Castling.CASTLEBLACKQ.getDestination());
            }
        }
    }

    private void handleKnightMove(Set<ChessBoardCoord> reachable, ChessSide player, ChessBoardCoord coord) {
        add(reachable, isOpponentOrFree(player, coord, +1, +2));
        add(reachable, isOpponentOrFree(player, coord, -1, +2));
        add(reachable, isOpponentOrFree(player, coord, -1, -2));
        add(reachable, isOpponentOrFree(player, coord, +1, -2));
        add(reachable, isOpponentOrFree(player, coord, +2, +1));
        add(reachable, isOpponentOrFree(player, coord, -2, +1));
        add(reachable, isOpponentOrFree(player, coord, -2, -1));
        add(reachable, isOpponentOrFree(player, coord, +2, -1));
    }

    private void handlePawnMove(Set<ChessBoardCoord> reachable, ChessBoardCoord coord, int dy, int baseRow, ChessSide player) {
        // Front move
        boolean frontIsFree = add(reachable, isFree(coord, 0, dy));

        // Double move if in origin row
        if (frontIsFree && coord.getRow() == baseRow + dy) {
            add(reachable, isFree(coord, 0, dy * 2));
        }

        // Sideways attack
        add(reachable, isOpponent(player, coord, 1, dy));
        add(reachable, isOpponent(player, coord, -1, dy));

        // En passant
        ChessBoardCoord lastPawn = position.getLastPawnDMove();
        if (lastPawn != null) {
            int dx = lastPawn.getCol() - coord.getCol();
            if (lastPawn.getRow() == coord.getRow() && Math.abs(dx) == 1) {
                add(reachable, isFree(coord, dx, dy));
            }
        }
    }

    private ChessBoardCoord isFree(ChessBoardCoord coord, int dx, int dy) {
        ChessBoardCoord result = null;

        ChessBoardCoord targetCoord = computeTargetCoord(coord, dx, dy);
        if (targetCoord != null && position.getPiece(targetCoord) == null) {
            result = targetCoord;
        }

        return result;
    }

    private ChessBoardCoord isOpponentOrFree(ChessSide player, ChessBoardCoord coord, int dx, int dy) {
        ChessBoardCoord result = isFree(coord, dx, dy);

        if (result == null) {
            result = isOpponent(player, coord, dx, dy);
        }

        return result;
    }

    private void isOpponentOrFreeRecursive(Collection<ChessBoardCoord> result, ChessSide player, ChessBoardCoord coord, int dx, int dy) {
        ChessBoardCoord targetCoord = isFree(coord, dx, dy);

        if (targetCoord == null) {
            targetCoord = isOpponent(player, coord, dx, dy);
        } else {
            isOpponentOrFreeRecursive(result, player, targetCoord, dx, dy);
        }

        if (targetCoord != null) {
            result.add(targetCoord);
        }
    }

    private ChessBoardCoord isOpponent(ChessSide player, ChessBoardCoord coord, int dx, int dy) {
        ChessBoardCoord result = null;

        ChessBoardCoord targetCoord = computeTargetCoord(coord, dx, dy);
        if (targetCoord != null) {
            ChessPiece coordPiece = position.getPiece(targetCoord);
            if (coordPiece != null && coordPiece.getSide() != player) {
                result = targetCoord;
            }
        }

        return result;
    }

    private boolean add(Set<ChessBoardCoord> reachable, ChessBoardCoord coord) {
        boolean result = false;

        if (coord != null) {
            reachable.add(coord);
            result = true;
        }

        return result;
    }

    private boolean add(Set<ChessBoardCoord> reachable, Collection<ChessBoardCoord> coords) {
        boolean result = false;

        if (coords != null) {
            reachable.addAll(coords);
            result = true;
        }

        return result;
    }

    private ChessBoardCoord computeTargetCoord(ChessBoardCoord coord, int dx, int dy) {
        ChessBoardCoord targetCoord = null;

        if (isBetween(0, coord.getCol() + dx, 8) && isBetween(0, coord.getRow() + dy, 8)) {
            targetCoord = coord.add(dx, dy);
        }

        return targetCoord;
    }

    private boolean isBetween(int a, int x, int b) {
        return a <= x && x < b;
    }
}
