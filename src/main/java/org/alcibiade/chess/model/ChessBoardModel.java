package org.alcibiade.chess.model;

import org.apache.commons.lang.ObjectUtils;

import java.io.Serializable;

public class ChessBoardModel implements ChessPosition, Serializable {

    private static final long serialVersionUID = 1;
    // Pieces organized in rows A1..H1, A1..B2, ...
    private ChessPiece[] pieces = new ChessPiece[64];
    // Castling flags in the following order: KQkq
    private boolean[] castlingFlags = new boolean[4];
    private ChessBoardCoord lastPawnDMove = null;
    private ChessSide nextPlayerTurn = ChessSide.WHITE;
    private int moveNumber;
    private int halfMoveClock;

    public void setPosition(ChessPosition position) {

        if (position instanceof ChessBoardModel) {
            ChessBoardModel otherModel = (ChessBoardModel) position;
            System.arraycopy(otherModel.pieces, 0, this.pieces, 0, 64);
            System.arraycopy(otherModel.castlingFlags, 0, this.castlingFlags, 0, 4);
            moveNumber = otherModel.moveNumber;
            halfMoveClock = otherModel.halfMoveClock;
        } else {
            for (ChessBoardCoord coord : ChessBoardCoord.getAllBoardCoords()) {
                setPiece(coord, position.getPiece(coord));
            }

            castlingFlags[0] = position.isCastlingAvailable(ChessSide.WHITE, true);
            castlingFlags[1] = position.isCastlingAvailable(ChessSide.WHITE, false);
            castlingFlags[2] = position.isCastlingAvailable(ChessSide.BLACK, true);
            castlingFlags[3] = position.isCastlingAvailable(ChessSide.BLACK, false);

            moveNumber = position.getMoveNumber();
            halfMoveClock = position.getHalfMoveClock();
        }

        nextPlayerTurn = position.getNextPlayerTurn();
        lastPawnDMove = position.getLastPawnDMove();
    }

    @Override
    public int getMoveNumber() {
        return this.moveNumber;
    }

    public void setMoveNumber(int moveNumber) {
        this.moveNumber = moveNumber;
    }

    @Override
    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    public void setHalfMoveClock(int halfMoveClock) {
        this.halfMoveClock = halfMoveClock;
    }

    @Override
    public ChessPiece getPiece(ChessBoardCoord coordinates) {
        return pieces[coordinates.getOffset()];
    }

    public void setPiece(ChessBoardCoord coord, ChessPiece piece) {
        pieces[coord.getOffset()] = piece;
    }

    public void clearSquare(ChessBoardCoord coord) {
        pieces[coord.getOffset()] = null;
    }

    public void clear() {
        for (int i = 0; i < 64; i++) {
            pieces[i] = null;
        }
    }

    public void movePiece(ChessBoardCoord src, ChessBoardCoord dst) {
        assert pieces[src.getOffset()] != null;
        pieces[dst.getOffset()] = pieces[src.getOffset()];
        pieces[src.getOffset()] = null;
    }

    public void setInitialPosition() {
        clear();

        for (int i = 0; i < 8; i++) {
            pieces[8 + i] = new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE);
            pieces[64 - 16 + i] = new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK);
        }

        setPiece(new ChessBoardCoord(0, 0), new ChessPiece(ChessPieceType.ROOK, ChessSide.WHITE));
        setPiece(new ChessBoardCoord(1, 0), new ChessPiece(ChessPieceType.KNIGHT, ChessSide.WHITE));
        setPiece(new ChessBoardCoord(2, 0), new ChessPiece(ChessPieceType.BISHOP, ChessSide.WHITE));
        setPiece(new ChessBoardCoord(3, 0), new ChessPiece(ChessPieceType.QUEEN, ChessSide.WHITE));
        setPiece(new ChessBoardCoord(4, 0), new ChessPiece(ChessPieceType.KING, ChessSide.WHITE));
        setPiece(new ChessBoardCoord(5, 0), new ChessPiece(ChessPieceType.BISHOP, ChessSide.WHITE));
        setPiece(new ChessBoardCoord(6, 0), new ChessPiece(ChessPieceType.KNIGHT, ChessSide.WHITE));
        setPiece(new ChessBoardCoord(7, 0), new ChessPiece(ChessPieceType.ROOK, ChessSide.WHITE));

        setPiece(new ChessBoardCoord(0, 7), new ChessPiece(ChessPieceType.ROOK, ChessSide.BLACK));
        setPiece(new ChessBoardCoord(1, 7), new ChessPiece(ChessPieceType.KNIGHT, ChessSide.BLACK));
        setPiece(new ChessBoardCoord(2, 7), new ChessPiece(ChessPieceType.BISHOP, ChessSide.BLACK));
        setPiece(new ChessBoardCoord(3, 7), new ChessPiece(ChessPieceType.QUEEN, ChessSide.BLACK));
        setPiece(new ChessBoardCoord(4, 7), new ChessPiece(ChessPieceType.KING, ChessSide.BLACK));
        setPiece(new ChessBoardCoord(5, 7), new ChessPiece(ChessPieceType.BISHOP, ChessSide.BLACK));
        setPiece(new ChessBoardCoord(6, 7), new ChessPiece(ChessPieceType.KNIGHT, ChessSide.BLACK));
        setPiece(new ChessBoardCoord(7, 7), new ChessPiece(ChessPieceType.ROOK, ChessSide.BLACK));

        castlingFlags[0] = true;
        castlingFlags[1] = true;
        castlingFlags[2] = true;
        castlingFlags[3] = true;
        lastPawnDMove = null;

        nextPlayerTurn = ChessSide.WHITE;

        moveNumber = 1;
        halfMoveClock = 0;
    }

    @Override
    public boolean isCastlingAvailable(ChessSide side, boolean kingside) {
        boolean result;

        if (side == ChessSide.BLACK) {
            if (kingside) {
                result = castlingFlags[2];
            } else {
                result = castlingFlags[3];
            }
        } else {
            if (kingside) {
                result = castlingFlags[0];
            } else {
                result = castlingFlags[1];
            }
        }

        return result;
    }

    public void setCastlingAvailable(ChessSide side, boolean kingside, boolean available) {
        if (side == ChessSide.BLACK) {
            if (kingside) {
                castlingFlags[2] = available;
            } else {
                castlingFlags[3] = available;
            }
        } else {
            if (kingside) {
                castlingFlags[0] = available;
            } else {
                castlingFlags[1] = available;
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;

        if (obj instanceof ChessBoardModel) {
            ChessBoardModel oModel = (ChessBoardModel) obj;
            result = true;

            for (int i = 0; i < castlingFlags.length; i++) {
                result = result && castlingFlags[i] == oModel.castlingFlags[i];
            }

            for (int i = 0; i < 64; i++) {
                result = result && ObjectUtils.equals(pieces[i], oModel.pieces[i]);
            }
        }

        return result;
    }

    @Override
    public int hashCode() {
        int result = 0;

        // We don't hash castling flags, only the pieces positions
        for (int i = 0; i < 64; i++) {
            result += (i * ObjectUtils.hashCode(pieces[i]));
        }

        return result;
    }

    public void nextPlayerTurn() {
        setNextPlayerTurn(getNextPlayerTurn().opposite());
    }

    @Override
    public ChessSide getNextPlayerTurn() {
        return this.nextPlayerTurn;
    }

    public void setNextPlayerTurn(ChessSide nextPlayerTurn) {
        this.nextPlayerTurn = nextPlayerTurn;
    }

    @Override
    public ChessBoardCoord getLastPawnDMove() {
        return lastPawnDMove;
    }

    public void setLastPawnDMove(ChessBoardCoord lastPawnDMove) {
        this.lastPawnDMove = lastPawnDMove;
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(nextPlayerTurn);
        text.append(' ');
        text.append(castlingFlags[0] ? "K" : "");
        text.append(castlingFlags[1] ? "Q" : "");
        text.append(castlingFlags[2] ? "k" : "");
        text.append(castlingFlags[3] ? "q" : "");

        if (lastPawnDMove != null) {
            text.append(' ');
            text.append(lastPawnDMove.getPgnCoordinates());
        }

        text.append('\n');

        for (int row = 7; row >= 0; row--) {
            for (int col = 0; col < 8; col++) {
                ChessPiece piece = getPiece(new ChessBoardCoord(col, row));
                if (piece == null) {
                    text.append('.');
                } else {
                    text.append(piece.getAsSingleCharacter());
                }

                text.append(' ');
            }

            text.append('\n');
        }
        return text.toString();
    }
}
