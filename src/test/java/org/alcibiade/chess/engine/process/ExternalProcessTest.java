package org.alcibiade.chess.engine.process;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Unit test external process management.
 */
public class ExternalProcessTest {

    @Test
    public void testProcessExecution() throws IOException {
        ExternalProcessFactory externalProcessFactory = new ExternalProcessFactory(30_000);
        ExternalProcess process = externalProcessFactory.run("echo", "a", "b", "c");
        String result = process.read(Pattern.compile("(a).*"));
        Assertions.assertThat(result).isEqualTo("a");
        process.close();
    }

    @Test
    public void testProcessExecutionMatcher() throws IOException {
        ExternalProcessFactory externalProcessFactory = new ExternalProcessFactory(30_000);
        ExternalProcess process = externalProcessFactory.run("echo", "a", "b", "c");
        String[] result = process.readForArray(Pattern.compile("(.*?) . (.*?)"));
        Assertions.assertThat(result[0]).isEqualTo("a");
        Assertions.assertThat(result[1]).isEqualTo("c");
        process.close();
    }
}
