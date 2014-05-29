package org.alcibiade.chess.engine;

import java.util.Collection;

public interface ChessEngineController {

    String computeNextMove(int depth, int random, Collection<String> moves) throws ChessEngineFailureException;
}
