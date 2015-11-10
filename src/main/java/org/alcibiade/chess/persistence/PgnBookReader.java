package org.alcibiade.chess.persistence;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.alcibiade.chess.persistence.PgnFormats.DATEFORMAT_PGN;
import static org.alcibiade.chess.persistence.PgnFormats.PATTERN_COMMENTS;

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
        Pattern header = Pattern.compile(PgnFormats.PATTERN_HEADER);
        String whitePlayerName = "White player";
        String blackPlayerName = "Black player";
        String result = "*";
        String event = null;
        String site = null;
        String round = null;
        Date gameDate = new Date();
        int readLines = 0;
        List<String> moves = new LinkedList<>();

        String line = bookReader.readLine();
        while (line != null) {
            readLines += 1;
            String preprocessed = preprocess(line);

            // An empty line after the moves marks the end of the moves.
            if (!moves.isEmpty() && preprocessed.isEmpty()) {
                break;
            }

            Matcher headerMatcher = header.matcher(preprocessed);

            if (headerMatcher.matches()) {
                String key = headerMatcher.group(1);
                String val = headerMatcher.group(2);
                log.debug("" + key + " = " + val);

                //noinspection StatementWithEmptyBody
                if ("?".equals(val)) {
                    // Ignore this field
                } else if (StringUtils.equalsIgnoreCase("white", key)) {
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
                } else if (StringUtils.equalsIgnoreCase("result", key)) {
                    result = val;
                } else if (StringUtils.equalsIgnoreCase("site", key)) {
                    site = val;
                } else if (StringUtils.equalsIgnoreCase("event", key)) {
                    event = val;
                } else if (StringUtils.equalsIgnoreCase("round", key)) {
                    round = val;
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

        return moves.isEmpty() ? null : new PgnGameModel(
                whitePlayerName, blackPlayerName, gameDate, result,
                event, site, round,
                moves);
    }

    private String preprocess(String line) {
        String result = line.trim();

        if (!result.startsWith("[")) {
            result = result.replaceAll(PATTERN_COMMENTS, "");
        }

        return result;
    }
}
