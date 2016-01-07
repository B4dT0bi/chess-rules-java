package org.alcibiade.chess.integration;

import org.alcibiade.chess.model.*;
import org.alcibiade.chess.model.boardupdates.ChessBoardUpdate;
import org.alcibiade.chess.persistence.PgnBookReader;
import org.alcibiade.chess.persistence.PgnGameModel;
import org.alcibiade.chess.persistence.PgnMarshaller;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class PgnMarshallerImplTest {

    @Autowired
    private PgnMarshaller pgnMarshaller;
    @Autowired
    private ChessRules chessRules;
    private Logger log = LoggerFactory.getLogger(PgnMarshallerImplTest.class);

    @Test
    public void testMoveMarshalling() throws IllegalMoveException {
        ChessBoardModel model = new ChessBoardModel();
        model.setInitialPosition();

        ChessBoardCoord e2 = new ChessBoardCoord("e2");
        ChessBoardCoord e4 = new ChessBoardCoord("e4");
        ChessMovePath e2e4 = new ChessMovePath(e2, e4);

        ChessBoardCoord g1 = new ChessBoardCoord("g1");
        ChessBoardCoord f3 = new ChessBoardCoord("f3");
        ChessMovePath g1f3 = new ChessMovePath(g1, f3);

        ChessBoardCoord e1 = new ChessBoardCoord("e1");
        ChessMovePath e1g1 = new ChessMovePath(e1, g1);

        assertEquals("e4", pgnMarshaller.convertMoveToPgn(model, e2e4));
        assertEquals("Nf3", pgnMarshaller.convertMoveToPgn(model, g1f3));

        model.setPiece(new ChessBoardCoord("f1"), null);
        model.setPiece(new ChessBoardCoord("g1"), null);
        assertEquals("O-O", pgnMarshaller.convertMoveToPgn(model, e1g1));
    }

    @Test
    public void testMoveUnMarshalling() throws IllegalMoveException, PgnMoveException {
        ChessBoardModel model = new ChessBoardModel();
        model.setInitialPosition();

        ChessBoardCoord e2 = new ChessBoardCoord("e2");
        ChessBoardCoord e4 = new ChessBoardCoord("e4");
        ChessBoardPath path = new ChessBoardPath(e2, e4);

        assertEquals(path, pgnMarshaller.convertPgnToMove(model, "Pe2-e4"));
        assertEquals(path, pgnMarshaller.convertPgnToMove(model, "e2e4"));
        assertEquals(path, pgnMarshaller.convertPgnToMove(model, "e4"));

        try {
            pgnMarshaller.convertPgnToMove(model, "e5");
            fail();
        } catch (PgnMoveException e) {
            // This move is invalid so we end up here
        }

        assertEquals(new ChessMovePath("e2", "e4", ChessPieceType.KNIGHT), pgnMarshaller
                .convertPgnToMove(model, "e4=N"));
    }

    @Test
    public void testPromotion() throws IllegalMoveException, PgnMoveException {
        ChessBoardModel model = new ChessBoardModel();
        model.setPiece(new ChessBoardCoord(2, 0), new ChessPiece(ChessPieceType.KING, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(2, 7), new ChessPiece(ChessPieceType.KING, ChessSide.BLACK));
        model.setPiece(new ChessBoardCoord(6, 6), new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE));
        model.setPiece(new ChessBoardCoord(7, 7), new ChessPiece(ChessPieceType.KNIGHT, ChessSide.BLACK));
        model.setNextPlayerTurn(ChessSide.WHITE);

        ChessMovePath movePathQ = pgnMarshaller.convertPgnToMove(model, "Pg7xh8=Q");
        assertEquals(ChessPieceType.QUEEN, movePathQ.getPromotedPieceType());
        assertEquals("g7", movePathQ.getSource().getPgnCoordinates());
        assertEquals("h8", movePathQ.getDestination().getPgnCoordinates());
        assertEquals("gxh8+=Q", pgnMarshaller.convertMoveToPgn(model, movePathQ));

        ChessMovePath movePathN = pgnMarshaller.convertPgnToMove(model, "Pg7xh8=N");
        assertEquals(ChessPieceType.KNIGHT, movePathN.getPromotedPieceType());
        assertEquals("g7", movePathN.getSource().getPgnCoordinates());
        assertEquals("h8", movePathN.getDestination().getPgnCoordinates());
        assertEquals("gxh8=N", pgnMarshaller.convertMoveToPgn(model, movePathN));
    }

    @Test
    public void testGameFromPhalanx() throws IllegalMoveException, PgnMoveException {
        ChessBoardModel model = new ChessBoardModel();
        model.setInitialPosition();

        String[] moves = {"Nc3", "d7d5", "Nf3", "b8c6"};

        for (String pgnMove : moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(model, pgnMove);
            List<ChessBoardUpdate> updatesForMove = chessRules.getUpdatesForMove(model, movePath);

            for (ChessBoardUpdate update : updatesForMove) {
                update.apply(model);
            }

            model.nextPlayerTurn();
        }
    }

    @Test
    public void testGameLoading() throws IOException, IllegalMoveException, PgnMoveException {
        InputStream is = this.getClass().getResourceAsStream("sample_game.pgn");
        Collection<String> moves = pgnMarshaller.importGame(is);
        assertEquals(85, moves.size());

        ChessBoardModel boardModel = new ChessBoardModel();
        boardModel.setPosition(chessRules.getInitialPosition());

        for (String move : moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(boardModel, move);
            log.debug("Move: " + move + " -> " + movePath);

            assertEquals(move, pgnMarshaller.convertMoveToPgn(boardModel, movePath));

            List<ChessBoardUpdate> updates = chessRules.getUpdatesForMove(boardModel, movePath);

            for (ChessBoardUpdate update : updates) {
                log.debug("    \\-- " + update);
                update.apply(boardModel);
            }

            boardModel.nextPlayerTurn();
        }

        ChessGameStatus status = chessRules.getStatus(boardModel);

        assertFalse(boardModel.isCastlingAvailable(ChessSide.WHITE, true));
        assertFalse(boardModel.isCastlingAvailable(ChessSide.WHITE, false));
        assertFalse(boardModel.isCastlingAvailable(ChessSide.BLACK, true));
        assertFalse(boardModel.isCastlingAvailable(ChessSide.BLACK, false));
        assertNull(boardModel.getLastPawnDMove());

        log.debug("Game status is: {}", status);
        log.debug("End postion is: {}", boardModel);
    }

    @Test
    public void testThreeKnightsSameTarget() throws IOException, IllegalMoveException, PgnMoveException {
        ChessBoardModel boardModel = new ChessBoardModel();
        ChessPiece wn = new ChessPiece(ChessPieceType.KNIGHT, ChessSide.WHITE);
        ChessMovePath a1b3 = new ChessMovePath("a1", "b3");

        boardModel.setPiece(new ChessBoardCoord("a1"), wn);
        assertEquals("Nb3", pgnMarshaller.convertMoveToPgn(boardModel, a1b3));

        boardModel.setPiece(new ChessBoardCoord("c1"), wn);
        assertEquals("Nab3", pgnMarshaller.convertMoveToPgn(boardModel, a1b3));

        boardModel.setPiece(new ChessBoardCoord("d2"), wn);
        assertEquals("Nab3", pgnMarshaller.convertMoveToPgn(boardModel, a1b3));

        ChessMovePath d2b3 = new ChessMovePath("d2", "b3");
        assertEquals("Ndb3", pgnMarshaller.convertMoveToPgn(boardModel, d2b3));
    }

    @Test
    public void testManyQueensSameTarget() throws IOException, IllegalMoveException, PgnMoveException {
        ChessBoardModel boardModel = new ChessBoardModel();
        ChessPiece wq = new ChessPiece(ChessPieceType.QUEEN, ChessSide.WHITE);
        ChessMovePath a1c3 = new ChessMovePath("a1", "c3");

        boardModel.setPiece(new ChessBoardCoord("a1"), wq);
        assertEquals("Qc3", pgnMarshaller.convertMoveToPgn(boardModel, a1c3));

        boardModel.setPiece(new ChessBoardCoord("c1"), wq);
        assertEquals("Qac3", pgnMarshaller.convertMoveToPgn(boardModel, a1c3));

        boardModel.setPiece(new ChessBoardCoord("e1"), wq);
        assertEquals("Qac3", pgnMarshaller.convertMoveToPgn(boardModel, a1c3));

        boardModel.setPiece(new ChessBoardCoord("a3"), wq);
        assertEquals("Qa1c3", pgnMarshaller.convertMoveToPgn(boardModel, a1c3));

        ChessMovePath a3c3 = new ChessMovePath("a3", "c3");
        assertEquals("Q3c3", pgnMarshaller.convertMoveToPgn(boardModel, a3c3));
    }

    @Test
    public void testGameDump() {
        String pgnGame = pgnMarshaller.exportGame("White player", "Black player", new Date(), new ArrayList<String>());
        Assertions.assertThat(pgnGame).contains("White player");
    }

    @Test
    public void testEnPassant() throws IllegalMoveException {
        ChessBoardModel boardModel = new ChessBoardModel();
        ChessPiece wPawn = new ChessPiece(ChessPieceType.PAWN, ChessSide.WHITE);
        ChessPiece bPawn = new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK);
        ChessBoardCoord d5 = new ChessBoardCoord("d5");

        boardModel.setPiece(new ChessBoardCoord("e5"), wPawn);
        boardModel.setPiece(d5, bPawn);
        boardModel.setLastPawnDMove(d5);

        ChessMovePath move = new ChessMovePath("e5", "d6");

        assertEquals("exd6", pgnMarshaller.convertMoveToPgn(boardModel, move));
    }

    @Test
    public void testPawnPromotionOnAttack() throws PgnMoveException, IllegalMoveException {
        String[] moves = {
                "Pe2-e4", "Nf6", "Pe4-e5", "h5", "Pe5xf6",
                "c5", "Nb1-c3", "Rh6", "Nc3-e4", "e6",
                "Pf6xg7", "Rh7"
        };

        ChessBoardModel model = new ChessBoardModel();
        model.setInitialPosition();

        for (String pgnMove : moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(model, pgnMove);
            List<ChessBoardUpdate> updatesForMove = chessRules.getUpdatesForMove(model, movePath);

            for (ChessBoardUpdate update : updatesForMove) {
                update.apply(model);
            }

            model.nextPlayerTurn();
        }

        ChessGameStatus status = chessRules.getStatus(model);
        assertEquals(ChessGameStatus.OPEN, status);

        assertEquals("q", pgnMarshaller.convertPgnToMove(model, "Pg7-g8").getPromotedPieceType().getShortName());
        assertEquals("q", pgnMarshaller.convertPgnToMove(model, "Pg7-g8=Q").getPromotedPieceType().getShortName());
        assertEquals("n", pgnMarshaller.convertPgnToMove(model, "Pg7-g8=N").getPromotedPieceType().getShortName());
        assertEquals("q", pgnMarshaller.convertPgnToMove(model, "Pg7-g8Q").getPromotedPieceType().getShortName());
        assertEquals("n", pgnMarshaller.convertPgnToMove(model, "Pg7-g8N").getPromotedPieceType().getShortName());
    }

    @Test
    public void testCastling() throws PgnMoveException, IllegalMoveException {
        String[] moves = {"e4", "c6", "Nf3", "d5",
                "d3", "dxe4", "dxe4", "Qxd1+",
                "Kxd1", "Bg4", "Bc4", "Nd7",
                "Ke1", "e6", "Ng5", "Ne5",
                "Bb3"};

        ChessBoardModel model = new ChessBoardModel();
        model.setInitialPosition();

        for (String pgnMove : moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(model, pgnMove);
            List<ChessBoardUpdate> updatesForMove = chessRules.getUpdatesForMove(model, movePath);

            for (ChessBoardUpdate update : updatesForMove) {
                update.apply(model);
            }

            model.nextPlayerTurn();
        }

        ChessMovePath castleMove = pgnMarshaller.convertPgnToMove(model, "O-O-O");
        assertEquals("e8", castleMove.getSource().getPgnCoordinates());
        assertEquals("c8", castleMove.getDestination().getPgnCoordinates());
        assertEquals("O-O-O", pgnMarshaller.convertMoveToPgn(model, castleMove));
    }

    @Test
    public void testPromotionInActualCase() throws PgnMoveException, IllegalMoveException {
        String[] moves = {"e4", "c5", "d4", "cxd4", "c3", "Nf6",
                "b4", "Nc6", "Bh6", "gxh6", "Na3", "dxc3", "Qd2",
                "cxd2+", "Ke2", "Nxe4", "Kf3", "d5", "Rc1", "Ne5+", "Ke2"};

        ChessBoardModel model = new ChessBoardModel();
        model.setInitialPosition();

        for (String pgnMove : moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(model, pgnMove);
            List<ChessBoardUpdate> updatesForMove = chessRules.getUpdatesForMove(model, movePath);

            for (ChessBoardUpdate update : updatesForMove) {
                update.apply(model);
            }

            model.nextPlayerTurn();
        }

        ChessMovePath promotionMovePath = new ChessMovePath(
                new ChessBoardCoord("d2"),
                new ChessBoardCoord("c1"),
                ChessPieceType.KNIGHT);

        assertEquals("dxc1+=N", pgnMarshaller.convertMoveToPgn(model, promotionMovePath));
    }

    @Test
    public void testMultiGameLoading() throws IOException, IllegalMoveException, PgnMoveException {
        try (InputStream gamesStream = this.getClass().getResourceAsStream("empty_game.pgn")) {
            Collection<String> moves = pgnMarshaller.importGame(gamesStream);
            assertEquals(85, moves.size());
        }

        try (InputStream gamesStream = this.getClass().getResourceAsStream("sample_game.pgn")) {
            Collection<String> moves = pgnMarshaller.importGame(gamesStream);
            assertEquals(85, moves.size());
        }

        try (InputStream gamesStream = this.getClass().getResourceAsStream("multiple_games.pgn")) {
            Collection<String> moves = pgnMarshaller.importGame(gamesStream);
            assertEquals(85, moves.size());
        }

        try (InputStream gamesStream = this.getClass().getResourceAsStream("multiple_games.pgn");
             PgnBookReader bookReader = new PgnBookReader(gamesStream)) {
            assertEquals(85, bookReader.readGame().getMoves().size());
            assertEquals(4, bookReader.readGame().getMoves().size());
            assertEquals(85, bookReader.readGame().getMoves().size());
            assertEquals(null, bookReader.readGame());
        }
    }

    @Test
    public void testInconsistentSpaces() throws IOException {
        try (InputStream gamesStream = this.getClass().getResourceAsStream("inconsistent_spaces.pgn");
             PgnBookReader bookReader = new PgnBookReader(gamesStream)) {
            Assertions.assertThat(bookReader.readGame().getMoves()).hasSize(85);
            Assertions.assertThat(bookReader.readGame().getMoves()).hasSize(4);
            Assertions.assertThat(bookReader.readGame().getMoves()).hasSize(85);
            Assertions.assertThat(bookReader.readGame()).isNull();
        }
    }

    @Test
    public void testComments() throws IOException {
        InputStream gamesStream = this.getClass().getResourceAsStream("comments.pgn");
        PgnBookReader bookReader = new PgnBookReader(gamesStream);
        PgnGameModel firstGame = bookReader.readGame();
        Assertions.assertThat(firstGame.getMoves()).doesNotContain("check").hasSize(54);
        Assertions.assertThat(bookReader.readGame()).isNull();

        ChessPosition position = chessRules.getInitialPosition();

        for (String pgn : firstGame.getMoves()) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(position, pgn);
            position = ChessHelper.applyMoveAndSwitch(chessRules, position, movePath);
        }
    }

    @Test
    public void testCheckAfterCastling() throws PgnMoveException, IllegalMoveException {
        // Sequence taken from Bareev vs. Adams 1991
        String moves = "d4 d6 Nf3 Bg4 c4 Nd7 Qb3 Rb8 Be3 c6 Nc3 Ngf6 Nd2 Qa5 d5 e5 "
                + "f3 Bf5 Bf2 e4 g4 Bg6 g5 Nh5 Bh3 Be7 Bxd7+ Kxd7 c5 cxd5 "
                + "Qxd5 Qb4 cxd6 Nf4 Qb3 Bxd6 Qxb4 Bxb4 Bg3 e3 Nb3 Ne6";

        ChessPosition position = chessRules.getInitialPosition();

        for (String pgn : moves.split(" ")) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(position, pgn);
            position = ChessHelper.applyMoveAndSwitch(chessRules, position, movePath);
        }

        // Next move's castling will create a check situation.
        ChessMovePath castleMovePath1 = pgnMarshaller.convertPgnToMove(position, "O-O-O");
        ChessMovePath castleMovePath2 = pgnMarshaller.convertPgnToMove(position, "O-O-O+");

        Assertions.assertThat(ChessHelper.isCheck(chessRules, position, castleMovePath1, true)).isEqualTo(true);
        Assertions.assertThat(castleMovePath1).isEqualToComparingFieldByField(castleMovePath2);
        Assertions.assertThat(pgnMarshaller.convertMoveToPgn(position, castleMovePath1)).isEqualTo("O-O-O+");
    }

    @Test(expected = PgnMoveException.class)
    public void testIllegalPgnMove() throws PgnMoveException {
        ChessPosition position = chessRules.getInitialPosition();
        pgnMarshaller.convertPgnToMove(position, "e5");
    }

    @Test(expected = IllegalMoveException.class)
    public void testIllegalRawMoveSource() throws IllegalMoveException {
        ChessPosition position = chessRules.getInitialPosition();
        ChessMovePath movePath = new ChessMovePath(new ChessBoardCoord(4, 2), new ChessBoardCoord(4, 4));
        pgnMarshaller.convertMoveToPgn(position, movePath);
    }

    @Test
    public void testBidirectionalTransform() throws IllegalMoveException, PgnMoveException {
        ChessPosition position = chessRules.getInitialPosition();
        ChessMovePath moveE4 = pgnMarshaller.convertPgnToMove(position, "e4");

        Assertions.assertThat(moveE4.getSource()).isEqualToComparingFieldByField(new ChessBoardCoord(4, 1));

        Assertions.assertThat(moveE4.getSource().getPgnCoordinates()).isEqualTo("e2");
        Assertions.assertThat(moveE4.getDestination().getPgnCoordinates()).isEqualTo("e4");
        String pgn = pgnMarshaller.convertMoveToPgn(position, moveE4);
        Assertions.assertThat(pgn).isEqualTo("e4");
    }

    @Test
    public void testCheckSuffix() throws IllegalMoveException, PgnMoveException {
        ChessPosition position = chessRules.getInitialPosition();
        String[] moves = {"d4", "c5", "e4", "Qa5+"};

        for (String moveText : moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(position, moveText);
            String moveTextFromPath = pgnMarshaller.convertMoveToPgn(position, movePath);
            Assertions.assertThat(moveTextFromPath).isEqualTo(moveText);
            position = ChessHelper.applyMoveAndSwitch(chessRules, position, movePath);
        }

    }

    @Test
    public void testCheckMateSuffix() throws IllegalMoveException, PgnMoveException {
        ChessPosition position = chessRules.getInitialPosition();
        String[] moves = {"g4", "e5", "f4", "Qh4#"};

        for (String moveText : moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(position, moveText);
            String moveTextFromPath = pgnMarshaller.convertMoveToPgn(position, movePath);
            Assertions.assertThat(moveTextFromPath).isEqualTo(moveText);
            position = ChessHelper.applyMoveAndSwitch(chessRules, position, movePath);
        }

    }
    
    @Test
    public void testIssue21() throws IllegalMoveException, PgnMoveException {
        ChessPosition position = chessRules.getInitialPosition();
        String[] moves = {"e4", "Nc6", "Nc3", "Nf6", "Nf3", "d5", "d4", "Nxe4", "Nxe4", 
                "dxe4", "Bh6", "gxh6", "Ba6", "exf3", "Qd3", "fxg2", "Qxh7", "gxh1+=Q", "Ke2"};
        
        for ( String moveText: moves) {
            ChessMovePath movePath = pgnMarshaller.convertPgnToMove(position, moveText);
            String moveTextFromPath = pgnMarshaller.convertMoveToPgn(position, movePath);
            Assertions.assertThat(moveTextFromPath).isEqualTo(moveText);
            position = ChessHelper.applyMoveAndSwitch(chessRules, position, movePath);
        }
        
    }
}