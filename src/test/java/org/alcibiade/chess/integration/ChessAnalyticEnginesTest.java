package org.alcibiade.chess.integration;

import org.alcibiade.chess.engine.ChessEngineAnalyticalController;
import org.alcibiade.chess.engine.ChessEngineFailureException;
import org.alcibiade.chess.engine.EngineAnalysisReport;
import org.alcibiade.chess.engine.GnuChessEngineImpl;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
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
            Assertions.assertThat(report.getExpectedMoves()).hasSize(8).are(
                    new Condition<String>() {
                        @Override
                        public boolean matches(String s) {
                            String trimmed = s.trim();
                            return trimmed.length() > 0;
                        }
                    });
        }
    }

    @Test
    public void testRegularExpression() {
        Assertions.assertThat(GnuChessEngineImpl.ANALYSIS_RESULT_PATTERN.matcher("8 +0 5 72286 Nc3 Nf6 Nf3 Nc6 d4 d5 Bf4 Bf5").matches()).isTrue();
        Assertions.assertThat(GnuChessEngineImpl.ANALYSIS_RESULT_PATTERN.matcher("8.         20/20        e4    ").matches()).isFalse();
        Assertions.assertThat(GnuChessEngineImpl.ANALYSIS_RESULT_PATTERN.matcher("8&   0.53     -4    735335\t e4 Nc6 d4 Nf6 Nd2").matches()).isFalse();
        Assertions.assertThat(GnuChessEngineImpl.ANALYSIS_RESULT_PATTERN.matcher("8   0     4    735335 e4 Nc6 d4 Nf6 Nd2").matches()).isTrue();
        Assertions.assertThat(GnuChessEngineImpl.ANALYSIS_RESULT_PATTERN.matcher("8.   0.53     -4    735335\t e4 Nc6 d4 Nf6 Nd2").matches()).isTrue();
    }

}
