package org.alcibiade.chess.integration;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.persistence.PgnBookReader;
import org.alcibiade.chess.persistence.PgnGameModel;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.Condition;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class GnuChessBookTest {

    private static final int GAMES_LIMIT = 1000;

    private Logger log = LoggerFactory.getLogger(GnuChessBookTest.class);
    @Autowired
    private ChessRules chessRules;
    @Autowired
    private PgnMarshaller pgnMarshaller;
    @Value("classpath:/book_1.02.pgn.gz")
    private Resource pgnBook;

    @Test
    public void testBookGames() throws IOException, PgnMoveException, IllegalMoveException {
        if (!pgnBook.exists()) {
            log.warn("Pgn book is not found.");
            return;
        }

        log.debug("Book length is {} kbytes", pgnBook.contentLength() / 1024);
        try (PgnBookReader bookReader = new PgnBookReader(new GZIPInputStream(pgnBook.getInputStream()))) {
            int gameIndex = 0;

            while (true) {
                gameIndex += 1;
                PgnGameModel game = bookReader.readGame();

                if (game == null || gameIndex > GAMES_LIMIT) {
                    break;
                }

                log.debug("Game {}: {}", String.format("%06d", gameIndex), game);

                ChessPosition position = chessRules.getInitialPosition();

                for (String pgnMove : game.getMoves()) {
                    log.trace("   - " + pgnMove);
                    ChessMovePath move = pgnMarshaller.convertPgnToMove(position, pgnMove);
                    position = ChessHelper.applyMoveAndSwitch(chessRules, position, move);
                }

                Assertions.assertThat(game.getResult()).isIn("0-1", "1-0", "1/2-1/2", "*");
                Assertions.assertThat(game.getRound()).is(new Condition<String>() {
                    @Override
                    public boolean matches(String s) {
                        return s == null || Integer.valueOf(s) > 0;
                    }
                });

            }
        }
    }

    @Test
    public void testGameModel() throws IOException, PgnMoveException, IllegalMoveException {
        if (!pgnBook.exists()) {
            log.warn("Pgn book is not found.");
            return;
        }

        try (PgnBookReader bookReader = new PgnBookReader(new GZIPInputStream(pgnBook.getInputStream()))) {
            PgnGameModel firstGame = bookReader.readGame();
            Assertions.assertThat(firstGame.getWhitePlayerName()).isEqualTo("Barden, Leonard W");
            Assertions.assertThat(firstGame.getBlackPlayerName()).isEqualTo("Adams, Michael");
            Assertions.assertThat(firstGame.getGameDate()).isWithinYear(1951);
            Assertions.assertThat(firstGame.getResult()).isEqualTo("1-0");
            Assertions.assertThat(firstGame.getRound()).isEqualTo(null);
            Assertions.assertThat(firstGame.getSite()).isEqualTo("Hastings");
            Assertions.assertThat(firstGame.getEvent()).isEqualTo(null);
        }
    }

    @Test
    public void testImportedGames() throws IOException, InterruptedException {
        Path importFolderPath = Paths.get("import");

        if (!Files.exists(importFolderPath)) {
            return;
        }

        DirectoryStream<Path> stream = Files.newDirectoryStream(importFolderPath);

        Set<Path> paths = new TreeSet<>();

        for (Path entry : stream) {
            Path fileName = entry.getFileName();
            if (fileName.toString().endsWith(".pgn")) {
                paths.add(entry);
            }
        }

        ExecutorService executor = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        for (final Path entry : paths) {
            executor.submit(new Runnable() {
                @Override
                public void run() {
                    log.debug("Reading file {}", entry);
                    try {
                        PgnBookReader bookReader = new PgnBookReader(Files.newInputStream(entry));
                        PgnGameModel game;
                        while ((game = bookReader.readGame()) != null) {
                            log.debug("   - {}", game);
                            log.debug("       Moves: {}", game.getMoves());

                            ChessPosition position = chessRules.getInitialPosition();

                            for (String pgnMove : game.getMoves()) {
                                ChessMovePath move = pgnMarshaller.convertPgnToMove(position, pgnMove);
                                position = ChessHelper.applyMoveAndSwitch(chessRules, position, move);
                            }
                        }

                        // Optionally delete the file to leave only files with issues
                        // Files.delete(entry);
                    } catch (IOException e) {
                        log.error("IO error while reading game", e);
                    } catch (ChessException e) {
                        log.debug("Chess rules failure", e);
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.DAYS);
    }
}
