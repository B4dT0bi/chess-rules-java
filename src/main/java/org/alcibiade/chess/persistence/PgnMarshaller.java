package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.persistence.pgn.PgnTag;
import org.alcibiade.chess.rules.ChessRules;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

public interface PgnMarshaller {

    String convertMoveToPgn(ChessPosition position, ChessMovePath move) throws IllegalMoveException;

    ChessMovePath convertPgnToMove(ChessPosition position, String pgnMove) throws PgnMoveException;

    String exportGame(String white, String black, Date startDate, Collection<String> moves);

    String exportGame(Collection<PgnTag> tags, AutoUpdateChessBoardModel chessBoardModel);

    Collection<String> importGame(InputStream pgnStream) throws IOException;

    PgnGameModel importGame(ChessRules rules, String pgnString) throws IOException;
}
