package org.alcibiade.chess.engine.process;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Create native processes.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
@Component
public class ExternalProcessFactory {

    @Value("${process.timeout:30000}")
    private long timeout;

    public ExternalProcess run(String... args) throws IOException {
        return new ExternalProcess(timeout, args);
    }
}
