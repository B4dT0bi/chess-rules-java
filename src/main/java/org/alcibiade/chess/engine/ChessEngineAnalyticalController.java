package org.alcibiade.chess.engine;

import java.util.Collection;

/**
 * Extend the engine interface to support analytical features.
 */
public interface ChessEngineAnalyticalController extends ChessEngineController {

    EngineAnalysisReport analyze(Collection<String> moves) throws ChessEngineFailureException;
}
