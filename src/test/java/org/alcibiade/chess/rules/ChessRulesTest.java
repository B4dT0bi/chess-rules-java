package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;
import org.alcibiade.chess.model.boardupdates.FlagUpdateCastling;
import org.alcibiade.chess.model.boardupdates.IncreaseHalfMoveClock;
import org.alcibiade.chess.model.boardupdates.PieceUpdateMove;
import org.alcibiade.chess.persistence.FenChessPosition;
import org.alcibiade.chess.persistence.FenMarshallerImpl;
import org.assertj.core.api.Assertions;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collection;
import java.util.Set;

public class ChessRulesTest {

    /**
     * Move kingside rook back and forth, and then try to castle.
     */
    @Test(expected = IllegalMoveException.class)
    public void testCastlingValidation() {
        ChessRulesImpl rules = new ChessRulesImpl();
        ChessPosition position = rules.getInitialPosition();

        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("e2", "e4"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("e7", "e5"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("f1", "e2"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("d7", "d5"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("g1", "f3"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("c7", "c5"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("h1", "g1"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("b7", "b5"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("g1", "h1"));
        position = ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("a7", "a5"));

        // Castling while rook has already moved
        ChessHelper.applyMoveAndSwitch(rules, position, new ChessMovePath("e1", "g1"));
    }

    @Test
    public void testCastlingMove() {
        ChessRules rules = new ChessRulesImpl();
        ChessBoardModel chessBoardModel = new ChessBoardModel();
        chessBoardModel.setPosition(new FenChessPosition("rnbqk2r/pp2ppbp/6p1/2p5/3P4/2PBPN2/P4PPP/R1BQK2R b KQkq - 2 8"));
        chessBoardModel = ChessHelper.applyMoveAndSwitch(rules, chessBoardModel, new ChessMovePath("e8", "g8"));
        Assertions.assertThat(chessBoardModel.isCastlingAvailable(ChessSide.BLACK, true)).isFalse();
        Assertions.assertThat(chessBoardModel.isCastlingAvailable(ChessSide.BLACK, false)).isFalse();
        Assertions.assertThat(chessBoardModel.isCastlingAvailable(ChessSide.WHITE, true)).isTrue();
        Assertions.assertThat(chessBoardModel.isCastlingAvailable(ChessSide.WHITE, false)).isTrue();
    }

    @Test
    public void testGetReachableDestinationsWithoutCheck() {
        ChessRules rules = new ChessRulesImpl();
        ChessBoardModel chessBoardModel = new ChessBoardModel();
        chessBoardModel.setPosition(new FenChessPosition("4R1rk/2p2rQp/p2q1P2/2p4N/7P/2P5/P5P1/7K b - - 1 1"));

        ChessBoardCoord rook = new ChessBoardCoord("g8");

        Set<ChessBoardCoord> destinationsDontCheckForCheck = rules.getReachableDestinations(chessBoardModel, rook, false);
        Assertions.assertThat(destinationsDontCheckForCheck).containsOnly(new ChessBoardCoord("e8"), new ChessBoardCoord("f8"), new ChessBoardCoord("g7"));

        Set<ChessBoardCoord> destinationsAvoidCheck = rules.getReachableDestinations(chessBoardModel, rook, true);
        Assertions.assertThat(destinationsAvoidCheck).isEmpty();

        ChessBoardCoord rook2 = new ChessBoardCoord("f7");

        Set<ChessBoardCoord> destinationsDontCheckForCheck2 = rules.getReachableDestinations(chessBoardModel, rook2, false);
        Assertions.assertThat(destinationsDontCheckForCheck2).containsOnly(new ChessBoardCoord("f6"), new ChessBoardCoord("d7"), new ChessBoardCoord("e7"), new ChessBoardCoord("g7"), new ChessBoardCoord("f8"));

        Set<ChessBoardCoord> destinationsAvoidCheck2 = rules.getReachableDestinations(chessBoardModel, rook2, true);
        Assertions.assertThat(destinationsAvoidCheck2).containsOnly(new ChessBoardCoord("g7"));

        chessBoardModel.setPosition(new FenChessPosition("8/4R2K/4kP2/2Q5/8/8/8/8 b - - 1 1"));

        ChessBoardCoord king = new ChessBoardCoord("e6");

        Set<ChessBoardCoord> destinationsDontCheckForCheckK = rules.getReachableDestinations(chessBoardModel, king, false);
        Assertions.assertThat(destinationsDontCheckForCheckK).containsOnly(new ChessBoardCoord("d7"), new ChessBoardCoord("d6"), new ChessBoardCoord("d5"), new ChessBoardCoord("e7"), new ChessBoardCoord("e5"), new ChessBoardCoord("f7"), new ChessBoardCoord("f6"), new ChessBoardCoord("f5"));

        Set<ChessBoardCoord> destinationsAvoidCheckK = rules.getReachableDestinations(chessBoardModel, king, true);
        Assertions.assertThat(destinationsAvoidCheckK).containsOnly(new ChessBoardCoord("f6"));

        chessBoardModel.setPosition(new FenChessPosition("8/8/7K/7n/7k/4N2n/5P1Q/8 b - - 0 1"));

        ChessBoardCoord knight = new ChessBoardCoord("h3");

        Set<ChessBoardCoord> destinationsDontCheckForCheckN = rules.getReachableDestinations(chessBoardModel, knight, false);
        Assertions.assertThat(destinationsDontCheckForCheckN).containsOnly(new ChessBoardCoord("g1"), new ChessBoardCoord("g5"), new ChessBoardCoord("f2"), new ChessBoardCoord("f4"));

        Set<ChessBoardCoord> destinationsAvoidCheckN = rules.getReachableDestinations(chessBoardModel, knight, true);
        Assertions.assertThat(destinationsAvoidCheckN).isEmpty();

    }

    @Test
    public void testGetCastlingMove() {
        ChessRules rules = new ChessRulesImpl();
        ChessBoardModel model = new ChessBoardModel();
        model.setPosition(new FenChessPosition("rnbqkbnr/pp2p2p/2pp1pp1/8/5P2/5NPB/PPPPP2P/RNBQK2R w KQkq -"));
        Set<ChessBoardCoord> destinations = rules.getReachableDestinations(model, new ChessBoardCoord("e1"), true);
        Assertions.assertThat(destinations).contains(new ChessBoardCoord("g1"));
        destinations = rules.getReachableDestinations(model, new ChessBoardCoord("e1"), false);
        Assertions.assertThat(destinations).contains(new ChessBoardCoord("g1"));

        Collection<ChessBoardUpdate> updates = rules.getUpdatesForMove(model, Castling.CASTLEWHITEK);
        Assertions.assertThat(updates).containsExactly(new PieceUpdateMove(new ChessBoardPath("h1", "f1")), new FlagUpdateCastling(ChessSide.WHITE, true), new FlagUpdateCastling(ChessSide.WHITE, false), new PieceUpdateMove(Castling.CASTLEWHITEK), new IncreaseHalfMoveClock());
    }
}
