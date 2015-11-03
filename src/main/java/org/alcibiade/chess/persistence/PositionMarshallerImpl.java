package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.*;
import org.springframework.stereotype.Component;

/**
 * Implementation of a position to/from string marshaller.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
@Component
public class PositionMarshallerImpl implements PositionMarshaller {

    @Override
    public String convertPositionToString(ChessPosition position) {
        StringBuilder text = new StringBuilder();

        text.append(position.getNextPlayerTurn().getShortName());

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                ChessBoardCoord coord = new ChessBoardCoord(x, y);
                ChessPiece piece = position.getPiece(coord);
                if (piece == null) {
                    text.append("-");
                } else {
                    String pieceLetter = piece.getType().getShortName();

                    if (piece.getSide() == ChessSide.WHITE) {
                        pieceLetter = pieceLetter.toUpperCase();
                    }

                    text.append(pieceLetter);
                }
            }
        }

        text.append(position.isCastlingAvailable(ChessSide.WHITE, true) ? "K" : "-");
        text.append(position.isCastlingAvailable(ChessSide.WHITE, false) ? "Q" : "-");
        text.append(position.isCastlingAvailable(ChessSide.BLACK, true) ? "k" : "-");
        text.append(position.isCastlingAvailable(ChessSide.BLACK, false) ? "q" : "-");

        ChessBoardCoord lastPawnDMove = position.getLastPawnDMove();
        text.append(lastPawnDMove == null ? "-" : lastPawnDMove.getPgnCoordinates().substring(0, 1));

        return text.toString();
    }

    @Override
    public ChessPosition convertStringToPosition(String text) {
        ChessBoardModel boardModel = new ChessBoardModel();

        boardModel.setNextPlayerTurn(ChessSide.valueOfShortName(text.substring(0, 1)));

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Character pieceChar = text.charAt(1 + x + y * 8);

                if (pieceChar != '-') {
                    ChessBoardCoord coord = new ChessBoardCoord(x, y);
                    ChessSide side = Character.isUpperCase(pieceChar) ? ChessSide.WHITE : ChessSide.BLACK;
                    ChessPiece piece = new ChessPiece(ChessPieceType.getPgnType(pieceChar.toString()), side);
                    boardModel.setPiece(coord, piece);
                }
            }
        }

        boardModel.setCastlingAvailable(ChessSide.WHITE, true, text.charAt(65) != '-');
        boardModel.setCastlingAvailable(ChessSide.WHITE, false, text.charAt(66) != '-');
        boardModel.setCastlingAvailable(ChessSide.BLACK, true, text.charAt(67) != '-');
        boardModel.setCastlingAvailable(ChessSide.BLACK, false, text.charAt(68) != '-');

        char dmove = text.charAt(69);
        if (dmove != '-') {
            String coord = null;

            switch (boardModel.getNextPlayerTurn()) {
                case BLACK:
                    coord = "" + dmove + "4";
                    break;
                case WHITE:
                    coord = "" + dmove + "5";
                    break;
            }

            ChessBoardCoord dmoveCoord = new ChessBoardCoord(coord);
            boardModel.setLastPawnDMove(dmoveCoord);
        }

        return boardModel;
    }
}
