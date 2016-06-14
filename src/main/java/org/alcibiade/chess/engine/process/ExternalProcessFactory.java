package org.alcibiade.chess.engine.process;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Create native processes.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
@Component
public class ExternalProcessFactory {

    @Value("${process.timeout:30000}")
    private long timeout;

    protected ExternalProcessFactory() {
    }

    public ExternalProcessFactory(long timeout) {
        this.timeout = timeout;
    }

    public long getTimeout() {
        return timeout;
    }

    public ExternalProcess run(String... args) throws IOException {
        return new ExternalProcess(timeout, args);
    }
}
