package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.AutoUpdateChessBoardModel;
import org.alcibiade.chess.persistence.pgn.*;
import org.alcibiade.chess.rules.ChessRules;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.alcibiade.chess.persistence.PgnFormats.PATTERN_COMMENTS;

/**
 * A PGN book is a file holding a collection of PGN games.
 *
 * @author Yannick Kirschhoffer <alcibiade@alcibiade.org>
 * @author Tobias Boese <tobias.boese@gmail.com>
 */
public class PgnBookReader implements Closeable {

    private BufferedReader bookReader;

    private ChessRules rules;

    private String lastLine = null;

    public PgnBookReader(InputStream bookStream) throws UnsupportedEncodingException {
        this(new InputStreamReader(bookStream, "UTF-8"));
    }

    public PgnBookReader(InputStream bookStream, ChessRules rules) throws UnsupportedEncodingException {
        this(new InputStreamReader(bookStream, "UTF-8"));
        this.rules = rules;
    }

    private PgnBookReader(Reader reader) {
        bookReader = new BufferedReader(reader);
    }

    public PgnBookReader(String pgnString, ChessRules rules) {
        this(new StringReader(pgnString));
        this.rules = rules;
    }

    @Override
    public void close() throws IOException {
        bookReader.close();
    }

    /**
     * Implement a lightweight PushBack reading mechanism.
     * @return
     * @throws IOException
     */
    private String readLine() throws IOException {
        if (lastLine != null) {
            String result = lastLine;
            lastLine = null;
            return result;
        }
        return bookReader.readLine();
    }

    public PgnGameModel readGame() throws IOException {
        return readGame(false);
    }

    /**
     * Read the next Game from the PGN Stream.
     * @param withChessposition when set to true create an Object of AutoUpdateChessBoardModel with all moves
     * @return a PgnGameModel object if successful, otherwise null
     * @throws IOException
     */
    public PgnGameModel readGame(final boolean withChessposition) throws IOException {
        Pattern header = Pattern.compile(PgnFormats.PATTERN_HEADER);
        List<String> moves = new LinkedList<>();
        boolean inComment = false;
        int variationCounter = 0;

        PgnGameModel result = new PgnGameModel();

        String line = readLine();
        while (line != null) {
            String preprocessed = preprocess(line);

            // An empty line after the moves marks the end of the moves.
            if (!moves.isEmpty() && line.startsWith("[")) {
                lastLine = line;
                break;
            }

            Matcher headerMatcher = header.matcher(preprocessed);

            if (headerMatcher.matches()) {
                String key = headerMatcher.group(1);
                String val = headerMatcher.group(2);

                //noinspection StatementWithEmptyBody
                if (StringUtils.equalsIgnoreCase("white", key)) {
                    result.addTag(new WhiteTag(val));
                } else if (StringUtils.equalsIgnoreCase("black", key)) {
                    result.addTag(new BlackTag(val));
                } else if (StringUtils.equalsIgnoreCase("date", key)) {
                    result.addTag(new DateTag(val));
                } else if (StringUtils.equalsIgnoreCase("result", key)) {
                    result.addTag(new ResultTag(val));
                } else if (StringUtils.equalsIgnoreCase("site", key)) {
                    result.addTag(new SiteTag(val));
                } else if (StringUtils.equalsIgnoreCase("event", key)) {
                    result.addTag(new EventTag(val));
                } else if (StringUtils.equalsIgnoreCase("round", key)) {
                    result.addTag(new RoundTag(val));
                } else {
                    result.addTag(new PgnTag(key, val));
                }
            } else {
                // Remove move numbers from the contents.                
                String[] tokens = preprocessed.replaceAll("(^| )[0-9]+\\.{1,3}", " ").split(" +");

                for (String token : tokens) {
                    if (token.contains("{")) {
                        inComment = true;
                    }

                    if (token.contains("}")) {
                        inComment = false;
                        continue;
                    }

                    if (inComment) {
                        continue;
                    }

                    if ("(".equals(token)) {
                        variationCounter++;
                    } else if (")".equals(token)) {
                        variationCounter--;
                    }

                    if (variationCounter > 0) {
                        continue; // FIXME : handle variations correctly instead of just ignoring them
                    }

                    // The first character may be a digit for end of game results like 1-0 or an "*"
                    if (!StringUtils.isEmpty(token) && Character.isLetter(token.charAt(0))) {
                        moves.add(token);
                    }
                }
            }

            line = readLine();
        }
        if (moves.isEmpty()) return null;
        result.setMoves(moves);
        if (rules != null && withChessposition) {
            AutoUpdateChessBoardModel chessBoardModel = new AutoUpdateChessBoardModel(rules);
            String fen = result.getTagValue(PgnTag.TAG_ID_FEN);
            if (fen != null) {
                chessBoardModel.setPosition(new FenChessPosition(fen));
            } else {
                chessBoardModel.setInitialPosition();
            }
            for (String move : moves) {
                chessBoardModel.update(SanHelper.convertSanToLan(rules, chessBoardModel, move));
            }
            result.setPosition(chessBoardModel);
        }
        return result;
    }

    private String preprocess(String line) {
        String result = line.trim();

        if (!result.startsWith("[")) {
            result = result.replaceAll(PATTERN_COMMENTS, "");
        }

        return result;
    }
}
