package org.alcibiade.chess.persistence;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.alcibiade.chess.persistence.PgnFormats.DATEFORMAT_PGN;
import static org.alcibiade.chess.persistence.PgnFormats.PATTERN_COMMENTS;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A PGN book is a file holding a collection of PGN games.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 */
public class PgnBookReader implements Closeable {

    private Logger log = LoggerFactory.getLogger(PgnBookReader.class);
    private BufferedReader bookReader;

    public PgnBookReader(InputStream bookStream) throws UnsupportedEncodingException {
        Reader reader = new InputStreamReader(bookStream, "UTF-8");
        bookReader = new BufferedReader(reader);
    }

    @Override
    public void close() throws IOException {
        bookReader.close();
    }

    public PgnGameModel readGame() throws IOException {
        String whitePlayerName = "White player";
        String blackPlayerName = "Black player";
        Date gameDate = new Date();
        int emptyLines = 0;
        int readLines = 0;
        List<String> moves = new LinkedList<>();

        String line = bookReader.readLine();
        while (line != null) {
            readLines += 1;
            String preprocessed = preprocess(line);

            if (preprocessed.isEmpty()) {
                emptyLines += 1;

                // A second empty line marks the enf of the moves.
                if (emptyLines == 2) {
                    break;
                }
            }

            // An empty line after the moves marks the end of the moves.
            if (!moves.isEmpty() && preprocessed.isEmpty()) {
                break;
            }

            Pattern header = Pattern.compile(PgnFormats.PATTERN_HEADER);
            Matcher headerMatcher = header.matcher(preprocessed);

            if (headerMatcher.matches()) {
                String key = headerMatcher.group(1);
                String val = headerMatcher.group(2);
                log.debug("" + key + " = " + val);

                if (StringUtils.equalsIgnoreCase("white", key)) {
                    whitePlayerName = val;
                } else if (StringUtils.equalsIgnoreCase("black", key)) {
                    blackPlayerName = val;
                } else if (StringUtils.equalsIgnoreCase("date", key)) {
                    SimpleDateFormat df = new SimpleDateFormat(DATEFORMAT_PGN);
                    try {
                        gameDate = df.parse(val.replaceAll("\\?\\?", "01"));
                    } catch (ParseException e) {
                        throw new IOException("Invalid date format in pgn header " + e);
                    }
                }
            } else {
                // Remove move numbers from the contents.                
                String[] tokens = preprocessed.replaceAll("(^| )[0-9]+\\.", " ").split(" +");

                for (String token : tokens) {
                    // The first character may be a digit for end of game results like 1-0 or an "*"
                    if (!StringUtils.isEmpty(token) && Character.isLetter(token.charAt(0))) {
                        moves.add(token);
                    }
                }
            }

            line = bookReader.readLine();
        }

        return readLines == 0 ? null : new PgnGameModel(whitePlayerName, blackPlayerName, gameDate, moves);
    }

    private String preprocess(String line) {
        String result = line.trim();

        if (!result.startsWith("[")) {
            result = result.replaceAll(PATTERN_COMMENTS, "");
        }

        return result;
    }
}
