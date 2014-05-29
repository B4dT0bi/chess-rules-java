package org.alcibiade.chess.rules;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessBoardPath;
import org.alcibiade.chess.model.ChessGameStatus;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPiece;
import org.alcibiade.chess.model.ChessPieceType;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.ChessSide;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;
import org.alcibiade.chess.model.boardupdates.FlagUpdateCastling;
import org.alcibiade.chess.model.boardupdates.FlagUpdatePawn;
import org.alcibiade.chess.model.boardupdates.PieceUpdateAdd;
import org.alcibiade.chess.model.boardupdates.PieceUpdateMove;
import org.alcibiade.chess.model.boardupdates.PieceUpdateRemove;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Chess rules reference implementation.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
@Component
public class ChessRulesImpl implements ChessRules {

    private Logger log = LoggerFactory.getLogger(ChessRulesImpl.class);

    @Override
    public Set<ChessMovePath> getAvailableMoves(ChessPosition position) {
        Set<ChessMovePath> availableMoves = new HashSet<>();

        ChessSide side = position.getNextPlayerTurn();

        PieceLocator locator = new PieceLocator(position);
        for (ChessBoardCoord pieceCoord : locator.locatePieces(side)) {
            for (ChessBoardCoord destination : getReachableDestinations(position, pieceCoord, true)) {
                ChessMovePath path = new ChessMovePath(pieceCoord, destination);

                try {
                    boolean check = ChessHelper.isCheck(this, position, path, false);
                    if (!check) {
                        availableMoves.add(path);
                    }
                } catch (IllegalMoveException ex) {
                    log.warn("Failed to compute available moves", ex);
                }
            }
        }

        return availableMoves;
    }

    @Override
    public Set<ChessBoardCoord> getReachableDestinations(ChessPosition position,
            ChessBoardCoord pieceCoords,
            boolean excludeCheckSituations) {
        PieceMoveManager moveManager = new PieceMoveManager(position);
        return moveManager.getReachableSquares(pieceCoords, excludeCheckSituations ? this : null);
    }

    @Override
    public Set<ChessBoardCoord> getAttackingPieces(ChessPosition position,
            ChessBoardCoord squarePosition) {

        Set<ChessBoardCoord> result = new HashSet<ChessBoardCoord>();
        ChessSide player = position.getNextPlayerTurn();
        ChessSide opponent = player.opposite();

        // Create a copy of the position and put a pawn on the checked square to
        // assert that pawn
        // menace is also detected in the case if an empty square.
        ChessBoardModel boardModel = new ChessBoardModel();
        boardModel.setPosition(position);
        boardModel.setPiece(squarePosition, new ChessPiece(ChessPieceType.PAWN, player));

        PieceLocator locator = new PieceLocator(boardModel);
        for (ChessBoardCoord pieceCoord : locator.locatePieces(opponent)) {
            Set<ChessBoardCoord> destinations = getReachableDestinations(boardModel, pieceCoord,
                    false);

            if (destinations.contains(squarePosition)) {
                result.add(pieceCoord);
            }
        }

        return result;
    }

    @Override
    public ChessPosition getInitialPosition() {
        ChessBoardModel boardmodel = new ChessBoardModel();
        boardmodel.setInitialPosition();
        return boardmodel;
    }

    @Override
    public ChessGameStatus getStatus(ChessPosition position) {
        ChessGameStatus status = ChessGameStatus.OPEN;

        if (getAvailableMoves(position).isEmpty()) {
            ChessSide nextPlayer = position.getNextPlayerTurn();
            ChessPiece playerKing = new ChessPiece(ChessPieceType.KING, nextPlayer);
            PieceLocator locator = new PieceLocator(position);
            ChessBoardCoord kingPosition = locator.locatePiece(playerKing).iterator().next();

            if (getAttackingPieces(position, kingPosition).isEmpty()) {
                status = ChessGameStatus.PAT;
            } else {
                if (nextPlayer == ChessSide.WHITE) {
                    status = ChessGameStatus.BLACKWON;
                } else {
                    status = ChessGameStatus.WHITEWON;
                }
            }
        }

        return status;
    }

    @Override
    public List<ChessBoardUpdate> getUpdatesForMove(ChessPosition position, ChessMovePath path)
            throws IllegalMoveException {
        List<ChessBoardUpdate> updates = new ArrayList<ChessBoardUpdate>();

        ChessPiece movedPiece = position.getPiece(path.getSource());
        assert movedPiece != null;

        ChessPieceType pieceType = movedPiece.getType();
        ChessSide player = position.getNextPlayerTurn();

        ChessPiece targetPiece = position.getPiece(path.getDestination());

        if (targetPiece != null) {
            updates.add(new PieceUpdateRemove(path.getDestination()));
        }

        // Castling
        considerCastlingMoves(position, path, updates, pieceType, player);
        considerRookImpacts(position, path, updates, pieceType, player, targetPiece);

        // Pawn 2 squares move
        if (pieceType == ChessPieceType.PAWN && path.get8Distance() == 2) {
            updates.add(new FlagUpdatePawn(path.getDestination()));
        } else if (position.getLastPawnDMove() != null) {
            updates.add(new FlagUpdatePawn(null));
        }

        updates.add(new PieceUpdateMove(path));

        // Promotion
        if (pieceType == ChessPieceType.PAWN) {
            ChessBoardCoord destination = path.getDestination();
            int destinationRow = destination.getRow();

            if (destinationRow == 0 || destinationRow == 7) {
                updates.add(new PieceUpdateRemove(destination));
                updates.add(new PieceUpdateAdd(destination, new ChessPiece(
                        path.getPromotedPieceType(), player)));
            }
        }

        // En passant capture
        if (pieceType == ChessPieceType.PAWN && targetPiece == null
                && path.getSource().getCol() != path.getDestination().getCol()) {
            updates.add(new PieceUpdateRemove(new ChessBoardCoord(path.getDestination().getCol(), path.
                    getSource().getRow())));
        }

        return updates;
    }

    private void considerRookImpacts(ChessPosition position, ChessMovePath path,
            List<ChessBoardUpdate> updates,
            ChessPieceType pieceType, ChessSide player, ChessPiece targetPiece) {
        // Rook move impact on castling flags
        if (pieceType == ChessPieceType.ROOK) {
            int row = player == ChessSide.WHITE ? 0 : 7;
            if (path.getSource().equals(new ChessBoardCoord(0, row))) {
                addCastlingFlagIfRequired(position, updates, player, false);
            } else if (path.getSource().equals(new ChessBoardCoord(7, row))) {
                addCastlingFlagIfRequired(position, updates, player, true);
            }
        }

        // Rook loss impact on castling flags
        if (targetPiece != null && targetPiece.getType() == ChessPieceType.ROOK) {
            int row = player == ChessSide.WHITE ? 7 : 0;
            ChessBoardCoord dest = path.getDestination();
            if (dest.getRow() == row) {
                if (dest.getCol() == 0) {
                    addCastlingFlagIfRequired(position, updates, player.opposite(), false);
                } else if (dest.getCol() == 7) {
                    addCastlingFlagIfRequired(position, updates, player.opposite(), true);
                }
            }
        }
    }

    private void considerCastlingMoves(ChessPosition position, ChessMovePath path,
            List<ChessBoardUpdate> updates,
            ChessPieceType pieceType, ChessSide player) throws IllegalMoveException {
        if (pieceType == ChessPieceType.KING) {
            if (path.equals(Castling.CASTLEWHITEQ)) {
                if (!position.isCastlingAvailable(ChessSide.WHITE, false)) {
                    throw new IllegalMoveException(path, "White queenside castling not available");
                }

                updates.add(new PieceUpdateMove(new ChessBoardPath("a1", "d1")));
            } else if (path.equals(Castling.CASTLEWHITEK)) {
                if (!position.isCastlingAvailable(ChessSide.WHITE, true)) {
                    throw new IllegalMoveException(path, "White kingside castling not available");
                }

                updates.add(new PieceUpdateMove(new ChessBoardPath("h1", "f1")));
            } else if (path.equals(Castling.CASTLEBLACKQ)) {
                if (!position.isCastlingAvailable(ChessSide.BLACK, false)) {
                    throw new IllegalMoveException(path, "Black queenside castling not available");
                }

                updates.add(new PieceUpdateMove(new ChessBoardPath("a8", "d8")));
            } else if (path.equals(Castling.CASTLEBLACKK)) {
                if (!position.isCastlingAvailable(ChessSide.BLACK, true)) {
                    throw new IllegalMoveException(path, "Black kingside castling not available");
                }

                updates.add(new PieceUpdateMove(new ChessBoardPath("h8", "f8")));
            }

            addCastlingFlagIfRequired(position, updates, player, true);
            addCastlingFlagIfRequired(position, updates, player, false);
        }
    }

    private void addCastlingFlagIfRequired(ChessPosition position, List<ChessBoardUpdate> updates,
            ChessSide side,
            boolean kingside) {
        if (position.isCastlingAvailable(side, kingside)) {
            updates.add(new FlagUpdateCastling(side, kingside));
        }
    }
}
