package org.alcibiade.chess.engine.process;

import org.assertj.core.api.Assertions;
import org.junit.Test;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Unit test external process management.
 */
public class ExternalProcessTest {

    @Test
    public void testProcessExecution() throws IOException {
        ExternalProcessFactory externalProcessFactory = new ExternalProcessFactory();
        ExternalProcess process = externalProcessFactory.run("echo", "a", "b", "c");
        String result = process.read(Pattern.compile("(a).*"));
        Assertions.assertThat(result).isEqualTo("a");
        process.close();
    }

    @Test
    public void testProcessExecutionMatcher() throws IOException {
        ExternalProcessFactory externalProcessFactory = new ExternalProcessFactory();
        ExternalProcess process = externalProcessFactory.run("echo", "a", "b", "c");
        Matcher result = process.readForMatcher(Pattern.compile("(.*?) . (.*?)"));
        Assertions.assertThat(result.group(1)).isEqualTo("a");
        Assertions.assertThat(result.group(2)).isEqualTo("c");
        process.close();
    }
}
