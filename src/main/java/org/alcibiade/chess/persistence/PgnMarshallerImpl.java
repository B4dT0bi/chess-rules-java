package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.rules.Castling;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.PieceLocator;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class PgnMarshallerImpl implements PgnMarshaller {

    private Logger log = LoggerFactory.getLogger(PgnMarshallerImpl.class);

    @Autowired
    private ChessRules chessRules;

    public PgnMarshallerImpl() {
    }

    public PgnMarshallerImpl(ChessRules chessRules) {
        this.chessRules = chessRules;
    }

    @Override
    public String convertMoveToPgn(ChessPosition position, ChessMovePath move) throws
            IllegalMoveException {
        String result;
        ChessPiece wk = new ChessPiece(ChessPieceType.KING, ChessSide.WHITE);
        ChessPiece bk = new ChessPiece(ChessPieceType.KING, ChessSide.BLACK);
        ChessBoardCoord e8 = new ChessBoardCoord("e8");
        ChessBoardCoord e1 = new ChessBoardCoord("e1");

        String checkMark = "";

        if (ChessHelper.isCheck(chessRules, position, move, true)) {
            if (ChessHelper.isCheckMate(chessRules, position, move)) {
                checkMark = "#";
            } else {
                checkMark = "+";
            }
        }

        if (move.equals(Castling.CASTLEBLACKK) && ObjectUtils.equals(bk, position.getPiece(e8))) {
            result = PgnFormats.PGN_CASTLE_K + checkMark;
        } else if (move.equals(Castling.CASTLEBLACKQ) && ObjectUtils.equals(bk, position.getPiece(e8))) {
            result = PgnFormats.PGN_CASTLE_Q + checkMark;
        } else if (move.equals(Castling.CASTLEWHITEK) && ObjectUtils.equals(wk, position.getPiece(e1))) {
            result = PgnFormats.PGN_CASTLE_K + checkMark;
        } else if (move.equals(Castling.CASTLEWHITEQ) && ObjectUtils.equals(wk, position.getPiece(e1))) {
            result = PgnFormats.PGN_CASTLE_Q + checkMark;
        } else {
            result = dumpStandardMove(position, move, checkMark);
        }

        return result;
    }

    private String dumpStandardMove(ChessPosition position, ChessMovePath move, String checkMark) throws
            IllegalMoveException {
        ChessPiece pieceSrc = position.getPiece(move.getSource());
        ChessPiece pieceDst = position.getPiece(move.getDestination());
        PieceLocator pieceLocator = new PieceLocator(position);

        Set<ChessMovePath> availableMoves = chessRules.getAvailableMoves(position);

        // Basically, any move to an occupied target is a capture.
        boolean isCapture = pieceDst != null;

        if (pieceSrc == null) {
            throw new IllegalMoveException(move.getSource());
        }

        // Special case for en passant move where the target is empty for the
        // capture. We can
        // assume that any diagonal pawn move is capture.
        if (pieceSrc.getType() == ChessPieceType.PAWN && move.getSource().getCol() != move.
                getDestination().getCol()) {
            isCapture = true;
        }

        StringBuilder pgn = new StringBuilder();
        if (pieceSrc.getType() != ChessPieceType.PAWN) {
            pgn.append(Character.toUpperCase(pieceSrc.getType().getShortName()));
        }

        boolean showSourceCol = false;
        boolean showSourceRow = false;
        boolean otherPieceCanReach = false;
        ChessBoardCoord source = move.getSource();

        Collection<ChessBoardCoord> samePieces = pieceLocator.locatePiece(pieceSrc);
        samePieces.remove(move.getSource());
        for (ChessBoardCoord samePiece : samePieces) {
            ChessMovePath samePieceMove = new ChessMovePath(samePiece, move.getDestination());

            if (availableMoves.contains(samePieceMove)) {
                otherPieceCanReach = true;

                if (samePiece.getCol() == source.getCol()) {
                    showSourceRow = true;
                }
                if (samePiece.getRow() == source.getRow()) {
                    showSourceCol = true;
                }
            }
        }

        // If we have several possible source, but no explicit row/col
        // ambiguity,
        // we use a col marker. Same goes for pawn attacks.
        boolean isPawnAttack = pieceSrc.getType() == ChessPieceType.PAWN && isCapture;
        if (!showSourceCol && !showSourceRow && (isPawnAttack || otherPieceCanReach)) {
            showSourceCol = true;
        }

        String sourcePgn = source.getPgnCoordinates();

        if (showSourceCol) {
            pgn.append(sourcePgn.charAt(0));
        }

        if (showSourceRow) {
            pgn.append(sourcePgn.charAt(1));
        }

        if (isCapture) {
            pgn.append("x");
        }

        pgn.append(move.getDestination().getPgnCoordinates());

        if (pieceSrc.getType() == ChessPieceType.PAWN && ((move.getDestination().getRow() == 7 && pieceSrc.getSide()
                == ChessSide.WHITE)
                || (move.getDestination().getRow() == 0 && pieceSrc.getSide() == ChessSide.BLACK))) {
            pgn.append("=");
            pgn.append(Character.toUpperCase(move.getPromotedPieceType().getShortName()));
        }

        pgn.append(checkMark);

        if (log.isDebugEnabled()) {
            log.debug("Position is " + position.toString());
            log.debug("Creating PGN notation for move " + move);
            log.debug("Possible sources are " + samePieces);
            log.debug("PGN: " + pgn);
        }

        return pgn.toString();
    }

    @Override
    public ChessMovePath convertPgnToMove(ChessPosition position, String pgnMove) throws
            PgnMoveException {
        ChessMovePath path;

        // Pre-process pgn input
        String trimmedPgn = pgnMove.trim();

        if (StringUtils.startsWithIgnoreCase(trimmedPgn, PgnFormats.PGN_CASTLE_Q)) {
            if (position.getNextPlayerTurn() == ChessSide.WHITE) {
                path = Castling.CASTLEWHITEQ;
            } else {
                path = Castling.CASTLEBLACKQ;
            }
        } else if (StringUtils.startsWithIgnoreCase(trimmedPgn, PgnFormats.PGN_CASTLE_K)) {
            if (position.getNextPlayerTurn() == ChessSide.WHITE) {
                path = Castling.CASTLEWHITEK;
            } else {
                path = Castling.CASTLEBLACKK;
            }
        } else {
            path = parseStandardMove(trimmedPgn, position);
        }

        return path;
    }

    protected ChessMovePath parseStandardMove(String pgnMove, ChessPosition position) throws
            PgnMoveException {
        Pattern pgnPattern = Pattern.compile(PgnFormats.PATTERN_PGN);
        Matcher pgnMatcher = pgnPattern.matcher(pgnMove);
        if (!pgnMatcher.matches()) {
            throw new PgnMoveException(pgnMove, "Does not match PGN syntax");
        }
        String pgnPiece = pgnMatcher.group(1);
        String pgnSourceX = pgnMatcher.group(2);
        String pgnSourceY = pgnMatcher.group(3);
        String pgnDestination = pgnMatcher.group(5);
        String pgnPromotion = pgnMatcher.group(6);

        if (pgnPiece.isEmpty() && !pgnSourceX.isEmpty() && !pgnSourceY.isEmpty()) {
            ChessBoardCoord sourceCoord = new ChessBoardCoord(pgnSourceX + pgnSourceY);
            ChessPiece piece = position.getPiece(sourceCoord);
            pgnPiece = piece.getType().getShortName().toString();
        }

        ChessBoardCoord dst = new ChessBoardCoord(pgnDestination);
        Set<ChessMovePath> availableMoves = chessRules.getAvailableMoves(position);
        Set<ChessBoardCoord> selectedSources = new HashSet<>();
        for (ChessBoardPath path : availableMoves) {
            if (!ObjectUtils.equals(path.getDestination(), dst)) {
                // Skip moves not aiming at destination square
                continue;
            }
            ChessBoardCoord attacker = path.getSource();
            ChessPiece piece = position.getPiece(attacker);
            assert piece != null;
            boolean selected = true;
            if (StringUtils.isEmpty(pgnPiece)) {
                if (!StringUtils.equalsIgnoreCase(piece.getType().getShortName().toString(),
                        ChessPieceType.PAWN.getShortName().toString())) {
                    selected = false;
                }
            } else {
                if (!StringUtils.equalsIgnoreCase(piece.getType().getShortName().toString(), pgnPiece)) {
                    selected = false;
                }
            }
            if (StringUtils.isNotEmpty(pgnSourceX)
                    && ChessBoardCoord.getColFromName(pgnSourceX) != attacker.getCol()) {
                selected = false;
            }
            if (StringUtils.isNotEmpty(pgnSourceY)
                    && ChessBoardCoord.getRowFromName(pgnSourceY) != attacker.getRow()) {
                selected = false;
            }
            if (selected) {
                selectedSources.add(attacker);
            }
        }
        if (selectedSources.isEmpty()) {
            if (log.isDebugEnabled()) {
                log.debug(position.toString());
                log.debug(availableMoves.toString());
            }
            throw new PgnMoveException(pgnMove, "No piece can reach square " + pgnDestination);
        } else if (selectedSources.size() > 1) {
            throw new PgnMoveException(pgnMove, "Several pieces can reach square " + pgnDestination);
        }

        ChessPieceType promoted = ChessPieceType.QUEEN;
        if (StringUtils.isNotEmpty(pgnPromotion)) {
            log.debug("PGN promotion is {}", pgnPromotion);
            assert pgnPromotion.length() == 1;
            promoted = ChessPieceType.getPgnType(pgnPromotion);
            assert promoted != null;
        }

        ChessBoardCoord src = selectedSources.iterator().next();
        ChessMovePath path = new ChessMovePath(src, dst, promoted);
        return path;
    }

    @Override
    public String exportGame(String white, String black, Date startDate, Collection<String> moves) {
        StringBuilder pgn = new StringBuilder();
        SimpleDateFormat df = new SimpleDateFormat(PgnFormats.DATEFORMAT_PGN);

        appendPgnHeader(pgn, "White", white);
        appendPgnHeader(pgn, "Black", black);
        appendPgnHeader(pgn, "Date", df.format(startDate));
        pgn.append("\n");

        StringBuilder line = new StringBuilder();
        int index = 0;

        for (String move : moves) {
            if (index % 2 == 0) {
                line.append(index / 2);
                line.append(". ");
            }

            line.append(move);
            line.append(' ');

            if (line.length() > 70) {
                pgn.append(line);
                pgn.append("\n");
                line.setLength(0);
            }
        }

        pgn.append(line);
        pgn.append("\n");

        return pgn.toString();
    }

    @Override
    public Collection<String> importGame(InputStream pgnStream) throws IOException {
        PgnBookReader bookReader = new PgnBookReader(pgnStream);
        return bookReader.readGame().getMoves();
    }

    private void appendPgnHeader(StringBuilder text, String name, String value) {
        text.append("[");
        text.append(name);
        text.append(" \"");
        text.append(value);
        text.append(" \"");
        text.append("]");
        text.append("\n");
    }
}
