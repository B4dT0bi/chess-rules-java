package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.*;

/**
 * Uses a FEN-String (https://en.wikipedia.org/wiki/Forsyth%E2%80%93Edwards_Notation) and provides a ChessPosition-Object.
 * <p/>
 * Created by Tobias Boese on 09.07.16.
 *
 * @author Tobias Boese
 */
public class FenChessPosition implements ChessPosition {
    private int moveNumber;
    private int halfMoveClock;
    private ChessSide nextPlayerTurn;
    private ChessBoardCoord lastPawnDMove;
    private ChessPiece[] pieces = new ChessPiece[64];
    private boolean[] castlingFlags = new boolean[4];

    public FenChessPosition(String fen) {
        // split fen string
        String tokens[] = fen.split(" ");
        // get pieces out of fen string
        String rows[] = tokens[0].split("/");
        for (int i = 0; i < rows.length; i++) {
            int colIndex = 0;
            for (char c : rows[i].toCharArray()) {
                ChessPieceType cpt = ChessPieceType.getPgnType("" + c);
                if (cpt != null) {
                    pieces[(7 - i) * 8 + colIndex] = new ChessPiece(cpt, isUpperCase(c) ? ChessSide.WHITE : ChessSide.BLACK);
                    colIndex++;
                } else {
                    colIndex += Integer.parseInt("" + c);
                }
            }
        }

        // get next player from fen string
        if ("w".equals(tokens[1])) {
            nextPlayerTurn = ChessSide.WHITE;
        } else {
            nextPlayerTurn = ChessSide.BLACK;
        }

        // get castlings
        for (char c : tokens[2].toCharArray()) {
            switch (c) {
                case 'K':
                    castlingFlags[0] = true;
                    break;
                case 'Q':
                    castlingFlags[1] = true;
                    break;
                case 'k':
                    castlingFlags[2] = true;
                    break;
                case 'q':
                    castlingFlags[3] = true;
                    break;
            }
        }

        if ("-".equals(tokens[3])) {
            lastPawnDMove = null;
        } else {
            lastPawnDMove = new ChessBoardCoord(tokens[3]).add(0, getNextPlayerTurn() == ChessSide.WHITE ? -1 : 1);
        }

        halfMoveClock = Integer.parseInt(tokens[4]);
        moveNumber = Integer.parseInt(tokens[5]);
    }

    private boolean isUpperCase(char c) {
        return ("" + c).toUpperCase().equals("" + c);
    }

    @Override
    public int getMoveNumber() {
        return moveNumber;
    }

    @Override
    public int getHalfMoveClock() {
        return halfMoveClock;
    }

    @Override
    public ChessPiece getPiece(final ChessBoardCoord coordinates) {
        return pieces[coordinates.getOffset()];
    }

    @Override
    public boolean isCastlingAvailable(final ChessSide side, final boolean kingside) {
        switch (side) {
            case WHITE:
                return kingside ? castlingFlags[0] : castlingFlags[1];
            case BLACK:
                return kingside ? castlingFlags[2] : castlingFlags[3];
        }
        return false;
    }

    @Override
    public ChessSide getNextPlayerTurn() {
        return nextPlayerTurn;
    }

    @Override
    public ChessBoardCoord getLastPawnDMove() {
        return lastPawnDMove;
    }
}
