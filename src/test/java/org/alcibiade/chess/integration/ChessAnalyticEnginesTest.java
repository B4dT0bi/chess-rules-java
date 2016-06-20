package org.alcibiade.chess.integration;

import org.alcibiade.chess.engine.ChessEngineAnalyticalController;
import org.alcibiade.chess.engine.ChessEngineFailureException;
import org.alcibiade.chess.engine.EngineAnalysisReport;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class ChessAnalyticEnginesTest {

    private Logger logger = LoggerFactory.getLogger(ChessAnalyticEnginesTest.class);
    @Autowired
    private List<ChessEngineAnalyticalController> engines;

    @Test
    public void testInitialPosition() throws ChessEngineFailureException {
        for (ChessEngineAnalyticalController engine : engines) {
            EngineAnalysisReport report = engine.analyze(new ArrayList<String>());
            logger.debug("Initial position report from {} is {}", engine, report);
            Assertions.assertThat(report.getPositionScore()).isGreaterThanOrEqualTo(0);
            Assertions.assertThat(report.getExpectedMoves()).hasSize(8);
        }
    }

}
