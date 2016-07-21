package org.alcibiade.chess.persistence;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.rules.Castling;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.PieceLocator;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A helper class for converting SAN to LAN moves and vice versa.
 *
 * @author Tobias Boese <tobias.boese@gmail.com>
 */
public class SanHelper {
    private static Pattern SAN_PATTERN = Pattern.compile("([PKNQRB]?)([a-h]?[1-8]?)([x]?)([a-h][1-8])((?:[=][NQRB])?)([+#]?)");

    /**
     * Convert a SAN move to a LAN move.
     * @param rules the ChessRules to be used
     * @param model the ChessBoardModel
     * @param san the move in Short Algebraic Notation
     * @return the move in Long Algebraic Notation
     */
    public static String convertSanToLan(final ChessRules rules, final ChessBoardModel model, final String san) {
        if (san == null) return null;
        if (san.startsWith(PgnFormats.PGN_CASTLE_Q)) {
            return model.getNextPlayerTurn() == ChessSide.BLACK ? Castling.CASTLEBLACKQ.toLanString() : Castling.CASTLEWHITEQ.toLanString();
        } else if (san.startsWith(PgnFormats.PGN_CASTLE_K)) {
            return model.getNextPlayerTurn() == ChessSide.BLACK ? Castling.CASTLEBLACKK.toLanString() : Castling.CASTLEWHITEK.toLanString();
        }

        Matcher matcher = SAN_PATTERN.matcher(san);

        matcher.find();
        ChessPieceType cpt = ChessPieceType.PAWN;
        if (!matcher.group(1).isEmpty()) {
            cpt = ChessPieceType.getPgnType(matcher.group(1));
        }

        ChessBoardCoord src = null;
        ChessBoardCoord dest = new ChessBoardCoord(matcher.group(4));

        ChessBoardModel pos = new ChessBoardModel();
        pos.setPosition(model);
        pos.nextPlayerTurn();
        if (!matcher.group(3).isEmpty()) {
            Set<ChessBoardCoord> attacker = rules.getAttackingPieces(pos, dest);
            // remove all attacker which do not conform with san-move
            Iterator<ChessBoardCoord> itAttacker = attacker.iterator();
            while (itAttacker.hasNext()) {
                ChessBoardCoord coord = itAttacker.next();
                if (pos.getPiece(coord).getType() != cpt) {
                    itAttacker.remove();
                } else {
                    Set<ChessBoardCoord> reachableCoords = rules.getReachableDestinations(model, coord, true);
                    if (!reachableCoords.contains(dest)) itAttacker.remove();
                }
            }

            if (attacker.size() > 1) {
                removeUnmatchedCoords(attacker, matcher.group(2));
                if (attacker.size() > 1) {
                    throw new PgnMoveException("ambiguous move (" + san + ")");
                }
            }
            if (attacker.isEmpty()) {
                throw new PgnMoveException("cannot find attacker of move " + san);
            }
            src = attacker.iterator().next();
        } else {
            Set<ChessBoardCoord> possibleSources = new HashSet<>();
            PieceLocator locator = new PieceLocator(model);

            for (ChessBoardCoord coord : locator.locatePieces(model.getNextPlayerTurn())) {
                ChessPiece cp = model.getPiece(coord);
                if (cp != null && cp.getType() == cpt) {
                    Set<ChessBoardCoord> targets = rules.getReachableDestinations(model, coord, true);
                    if (targets.contains(dest))
                        possibleSources.add(coord);
                }
            }

            if (possibleSources.size() > 1) {
                removeUnmatchedCoords(possibleSources, matcher.group(2));
                if (possibleSources.size() > 1) {
                    throw new PgnMoveException("ambiguous move (" + san + ")");
                }
            }
            if (possibleSources.isEmpty()) {
                throw new PgnMoveException("cannot find correct source of move " + san);
            }
            src = possibleSources.iterator().next();
        }
        String promotion = matcher.group(5);
        return src.getPgnCoordinates()
                + dest.getPgnCoordinates()
                + ((promotion != null && !promotion.isEmpty()) ? promotion.substring(1).toLowerCase() : "");
    }

    private static void removeUnmatchedCoords(Set<ChessBoardCoord> coords, String detail) {
        if (detail == null || detail.isEmpty()) return;
        Iterator<ChessBoardCoord> iterator = coords.iterator();
        while (iterator.hasNext()) {
            ChessBoardCoord coord = iterator.next();
            if (detail.length() == 1 && !coord.getPgnCoordinates().startsWith(detail) && !coord.getPgnCoordinates().endsWith(detail)) {
                iterator.remove();
            } else if (detail.length() == 2 && !coord.getPgnCoordinates().equals(detail)) {
                iterator.remove();
            }
        }
    }

    /**
     * Convert a ChessMovePath to a move in the Short Algebraic Notation.
     *
     * @param rules
     * @param model
     * @param move
     * @return
     */
    public static String getSanMove(ChessRules rules, ChessBoardModel model, ChessMovePath move) {
        if (Castling.CASTLEBLACKK.equals(move) || Castling.CASTLEWHITEK.equals(move)) {
            return PgnFormats.PGN_CASTLE_K;
        } else if (Castling.CASTLEBLACKQ.equals(move) || Castling.CASTLEWHITEQ.equals(move)) {
            return PgnFormats.PGN_CASTLE_Q;
        }
        ChessPiece cpSrc = model.getPiece(move.getSource());
        ChessPiece cpDst = model.getPiece(move.getDestination());
        String result = (cpSrc.getType() == ChessPieceType.PAWN ? "" : cpSrc.getType().getShortName().toString().toUpperCase());
        if (cpDst != null) {
            ChessBoardModel pos = new ChessBoardModel();
            pos.setPosition(model);
            pos.nextPlayerTurn();
            if (hasAmbiguousAttacker(model, rules.getAttackingPieces(pos, move.getDestination())) || cpSrc.getType() == ChessPieceType.PAWN) {
                result += move.getSource().getPgnCoordinates().substring(0, 1) + "x";
            } else {
                result += "x";
            }
        }
        if (cpSrc.getType() == ChessPieceType.PAWN && (move.getDestination().getRow() == 0 || move.getDestination().getRow() == 7) && move.getPromotedPieceType() != null) {
            result += "=" + move.getPromotedPieceType().getShortName().toString().toUpperCase();
        }

        return result
                + move.getDestination().getPgnCoordinates()
                + (ChessHelper.isCheckMate(rules, model, move) ? "#" : (ChessHelper.isCheck(rules, model, move, true) ? "+" : "")); // TODO : add en passant
    }

    /**
     * Check if the attacker is ambiguous.
     *
     * @param model
     * @param attacker
     * @return
     */
    private static boolean hasAmbiguousAttacker(ChessBoardModel model, Set<ChessBoardCoord> attacker) {
        if (attacker.size() <= 1) return false;
        Set<ChessPieceType> cpts = new HashSet<>();
        for (ChessBoardCoord coord : attacker) {
            ChessPiece cp = model.getPiece(coord);
            if (cpts.contains(cp.getType())) return true;
            cpts.add(cp.getType());
        }
        return false;
    }
}