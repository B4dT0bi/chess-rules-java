package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.ChessPosition;

/**
 * Persist position without history.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
public interface PositionMarshaller {

    String convertPositionToString(ChessPosition position);

    ChessPosition convertStringToPosition(String text);

}
