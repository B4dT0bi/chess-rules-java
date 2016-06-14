package org.alcibiade.chess.engine;

import java.util.List;

/**
 * Report on a given position provided by an engine.
 */
public class EngineAnalysisReport {

    private int positionScore;

    private List<String> expectedMoves;

    protected EngineAnalysisReport() {
    }

    public EngineAnalysisReport(int positionScore, List<String> expectedMoves) {
        this.positionScore = positionScore;
        this.expectedMoves = expectedMoves;
    }

    public int getPositionScore() {
        return positionScore;
    }

    public List<String> getExpectedMoves() {
        return expectedMoves;
    }

    @Override
    public String toString() {
        return "EngineAnalysisReport{" +
                "positionScore=" + positionScore +
                ", expectedMoves=" + expectedMoves +
                '}';
    }
}
