package org.alcibiade.chess.engine.process;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Arrays;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExternalProcess implements Closeable {

    private Logger log = LoggerFactory.getLogger(ExternalProcess.class);
    private Process process;
    private Writer processWriter;
    private BufferedReader processReader;
    private Timer watchdogTimer;

    public ExternalProcess(long timeout, String... args) throws IOException {
        log.debug("Starting process " + Arrays.toString(args));

        ProcessBuilder pBuilder = new ProcessBuilder(args);
        Map<String, String> env = pBuilder.environment();
        String systemPath = System.getenv("PATH");
        log.debug("System path is " + systemPath);
        env.put("PATH", systemPath);

        process = pBuilder.start();

        // Set timeout watchdog

        watchdogTimer = new Timer();
        watchdogTimer.schedule(new InterruptProcessTask(process), timeout);

        processWriter = new OutputStreamWriter(process.getOutputStream());
        processReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    @Override
    public void close() throws IOException {
        watchdogTimer.cancel();

        if (processWriter != null) {
            processWriter.close();
        }

        if (process != null) {
            try {
                int code = process.waitFor();
                log.debug("Process ended with status " + code);
                assert code == 0;
            } catch (InterruptedException e) {
                log.warn("Interrupted while waiting for process to end");
            }
        }
    }

    public void write(String text) throws IOException {
        log.debug("Sending data:\n" + text);
        processWriter.write(text);
        processWriter.flush();
    }


    public String read(Pattern resultPattern, Pattern... errorPatterns) throws IOException {
        String result = null;
        boolean eof = false;

        do {
            String line = processReader.readLine();
            log.trace("Process|OUT|{}", line);

            if (line == null) {
                eof = true;
            } else {
                for (Pattern errorPattern : errorPatterns) {
                    Matcher errorMatcher = errorPattern.matcher(line);
                    if (errorMatcher.matches()) {
                        throw new IOException("External process error: " + line);
                    }
                }

                Matcher nextMoveMatcher = resultPattern.matcher(line);
                if (nextMoveMatcher.matches()) {
                    result = nextMoveMatcher.group(1);
                }
            }

        } while (result == null && !eof);

        log.trace("Process|RES|{}", result);

        return result;
    }

    private class InterruptProcessTask extends TimerTask {

        private Process process;

        public InterruptProcessTask(Process process) {
            this.process = process;
        }

        @Override
        public void run() {
            log.warn("Interrupting external process " + process);
            process.destroy();
        }
    }
}
