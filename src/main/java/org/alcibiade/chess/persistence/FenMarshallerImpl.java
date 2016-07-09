package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessPiece;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.ChessSide;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

/**
 * Implementation of a position to/from string marshaller in the Forsyth–Edwards Notation (FEN) format.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
@Component
@Qualifier("fen")
public class FenMarshallerImpl implements PositionMarshaller {

    @Override
    public String convertPositionToString(ChessPosition position) {
        StringBuilder text = new StringBuilder();

        text.append(buildBoardRepresentation(position));
        text.append(' ');
        text.append(position.getNextPlayerTurn().getShortName());
        text.append(' ');
        text.append(buildCastlingFlags(position));
        text.append(' ');
        text.append(buildEnPassantTarget(position));
        text.append(' ');
        text.append(position.getHalfMoveClock());
        text.append(' ');
        text.append(position.getMoveNumber());

        return text.toString();
    }

    private String buildEnPassantTarget(ChessPosition position) {
        StringBuilder text = new StringBuilder();

        ChessBoardCoord lastPawnDMove = position.getLastPawnDMove();
        if (lastPawnDMove == null) {
            text.append("-");
        } else {
            ChessBoardCoord target = lastPawnDMove.add(0, position.getNextPlayerTurn() == ChessSide.WHITE ? 1 : -1);
            text.append(target.getPgnCoordinates());
        }

        return text.toString();
    }

    private String buildBoardRepresentation(ChessPosition position) {
        StringBuilder text = new StringBuilder();

        for (int row = 7; row >= 0; row--) {
            if (text.length() > 0) {
                text.append("/");
            }

            int col = 0;

            while (col < 8) {
                ChessBoardCoord coord = new ChessBoardCoord(col, row);
                ChessPiece piece = position.getPiece(coord);
                if (piece == null) {
                    int whites = 1;

                    while (col + whites < 8 && position.getPiece(coord.add(new ChessBoardCoord(whites, 0))) == null) {
                        whites++;
                    }

                    text.append(Integer.toString(whites));

                    col += whites;
                } else {
                    col += 1;
                    text.append(piece.getAsSingleCharacter());
                }
            }
        }

        return text.toString();
    }

    private String buildCastlingFlags(ChessPosition position) {
        StringBuilder text = new StringBuilder();

        text.append(position.isCastlingAvailable(ChessSide.WHITE, true) ? "K" : "");
        text.append(position.isCastlingAvailable(ChessSide.WHITE, false) ? "Q" : "");
        text.append(position.isCastlingAvailable(ChessSide.BLACK, true) ? "k" : "");
        text.append(position.isCastlingAvailable(ChessSide.BLACK, false) ? "q" : "");

        if (text.length() == 0) {
            text.append("-");
        }

        return text.toString();
    }

    @Override
    public ChessPosition convertStringToPosition(String text) {
        return new FenChessPosition(text);
    }
}
