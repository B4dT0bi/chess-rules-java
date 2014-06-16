package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessPiece;
import org.alcibiade.chess.model.ChessPieceType;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.ChessSide;
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
        text.append(lastPawnDMove == null ? "-" : lastPawnDMove.getPgnCoordinates());

        return text.toString();
    }

    @Override
    public ChessPosition convertStringToPosition(String text) {
        ChessBoardModel boardModel = new ChessBoardModel();

        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                Character pieceChar = text.charAt(x + y * 8);

                if (pieceChar != '-') {
                    ChessBoardCoord coord = new ChessBoardCoord(x, y);
                    ChessSide side = Character.isUpperCase(pieceChar) ? ChessSide.WHITE : ChessSide.BLACK;
                    ChessPiece piece = new ChessPiece(ChessPieceType.getPgnType(pieceChar.toString()), side);
                    boardModel.setPiece(coord, piece);
                }
            }
        }

        boardModel.setCastlingAvailable(ChessSide.WHITE, true, text.charAt(64) != '-');
        boardModel.setCastlingAvailable(ChessSide.WHITE, false, text.charAt(65) != '-');
        boardModel.setCastlingAvailable(ChessSide.BLACK, true, text.charAt(66) != '-');
        boardModel.setCastlingAvailable(ChessSide.BLACK, false, text.charAt(67) != '-');

        String dmove = text.substring(68);
        if (dmove.length() > 1) {
            ChessBoardCoord dmoveCoord = new ChessBoardCoord(dmove);
            boardModel.setLastPawnDMove(dmoveCoord);
        }

        return boardModel;
    }
}
