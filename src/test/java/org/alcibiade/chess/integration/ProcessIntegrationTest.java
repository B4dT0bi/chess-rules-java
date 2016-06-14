package org.alcibiade.chess.integration;

import org.alcibiade.chess.engine.process.ExternalProcessFactory;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Test external process integration in the Spring context.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class ProcessIntegrationTest {

    @Autowired
    private ExternalProcessFactory externalProcessFactory;

    @Test
    public void testTimeout() {
        Assertions.assertThat(externalProcessFactory.getTimeout()).isEqualTo(30_000);
    }
}
