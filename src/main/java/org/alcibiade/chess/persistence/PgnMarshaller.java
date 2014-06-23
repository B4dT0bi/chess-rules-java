package org.alcibiade.chess.persistence;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.PgnMoveException;

public interface PgnMarshaller {

    String convertMoveToPgn(ChessPosition position, ChessMovePath move) throws IllegalMoveException;

    ChessMovePath convertPgnToMove(ChessPosition position, String pgnMove) throws PgnMoveException;

    String exportGame(String white, String black, Date startDate, Collection<String> moves);

    Collection<String> importGame(InputStream pgnStream) throws IOException;
}
