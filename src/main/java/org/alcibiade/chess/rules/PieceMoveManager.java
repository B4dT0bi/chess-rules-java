package org.alcibiade.chess.rules;

import java.util.HashSet;
import java.util.Set;
import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessPiece;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.ChessSide;

public class PieceMoveManager {

    private ChessPosition position;

    public PieceMoveManager(ChessPosition position) {
        this.position = position;
    }

    // SUPPRESS CHECKSTYLE NPath
    public Set<ChessBoardCoord> getReachableSquares(ChessBoardCoord coord, ChessRules rules) {
        Set<ChessBoardCoord> reachable = new HashSet<ChessBoardCoord>();
        ChessPiece piece = position.getPiece(coord);

        if (piece != null) {
            ChessSide player = piece.getSide();
            int dy = player == ChessSide.WHITE ? 1 : -1;
            int baseRow = player == ChessSide.WHITE ? 0 : 7;

            switch (piece.getType()) {
                case PAWN:
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
                    break;

                case KNIGHT:
                    add(reachable, isOpponentOrFree(player, coord, +1, +2));
                    add(reachable, isOpponentOrFree(player, coord, -1, +2));
                    add(reachable, isOpponentOrFree(player, coord, -1, -2));
                    add(reachable, isOpponentOrFree(player, coord, +1, -2));
                    add(reachable, isOpponentOrFree(player, coord, +2, +1));
                    add(reachable, isOpponentOrFree(player, coord, -2, +1));
                    add(reachable, isOpponentOrFree(player, coord, -2, -1));
                    add(reachable, isOpponentOrFree(player, coord, +2, -1));
                    break;

                case KING:
                    add(reachable, isOpponentOrFree(player, coord, +1, +1));
                    add(reachable, isOpponentOrFree(player, coord, +0, +1));
                    add(reachable, isOpponentOrFree(player, coord, -1, +1));
                    add(reachable, isOpponentOrFree(player, coord, +1, +0));
                    add(reachable, isOpponentOrFree(player, coord, -1, +0));
                    add(reachable, isOpponentOrFree(player, coord, +1, -1));
                    add(reachable, isOpponentOrFree(player, coord, +0, -1));
                    add(reachable, isOpponentOrFree(player, coord, -1, -1));

                    if (position.isCastlingAvailable(player, true)) {
                        boolean positionOk = true;
                        positionOk = positionOk && position.getPiece(new ChessBoardCoord(5, baseRow))
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
                        boolean positionOk = true;
                        positionOk = positionOk && position.getPiece(new ChessBoardCoord(1, baseRow))
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

                    break;

                case ROOK:
                    add(reachable, isOpponentOrFreeRecursive(player, coord, +1, 0));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, -1, 0));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, 0, +1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, 0, -1));
                    break;

                case BISHOP:
                    add(reachable, isOpponentOrFreeRecursive(player, coord, +1, +1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, -1, +1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, +1, -1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, -1, -1));
                    break;

                case QUEEN:
                    add(reachable, isOpponentOrFreeRecursive(player, coord, +1, 0));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, -1, 0));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, 0, +1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, 0, -1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, +1, +1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, -1, +1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, +1, -1));
                    add(reachable, isOpponentOrFreeRecursive(player, coord, -1, -1));
                    break;
            }
        }

        return reachable;
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

    private Set<ChessBoardCoord> isOpponentOrFreeRecursive(ChessSide player, ChessBoardCoord coord,
            int dx, int dy) {
        Set<ChessBoardCoord> result = new HashSet<ChessBoardCoord>();
        ChessBoardCoord targetCoord = isFree(coord, dx, dy);

        if (targetCoord == null) {
            targetCoord = isOpponent(player, coord, dx, dy);
        } else {
            result.addAll(isOpponentOrFreeRecursive(player, targetCoord, dx, dy));
        }

        if (targetCoord != null) {
            result.add(targetCoord);
        }

        return result;
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

    private boolean add(Set<ChessBoardCoord> reachable, Set<ChessBoardCoord> coords) {
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
