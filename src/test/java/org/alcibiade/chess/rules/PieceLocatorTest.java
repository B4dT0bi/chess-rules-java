package org.alcibiade.chess.rules;

import org.alcibiade.chess.model.ChessBoardCoord;
import org.alcibiade.chess.model.ChessBoardModel;
import org.alcibiade.chess.model.ChessPiece;
import org.alcibiade.chess.model.ChessPieceType;
import org.alcibiade.chess.model.ChessSide;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class PieceLocatorTest {

    @Test
    public void testLocatePiece() {
        ChessBoardModel boardModel = new ChessBoardModel();
        boardModel.setInitialPosition();

        PieceLocator locator = new PieceLocator(boardModel);

        assertEquals(new ChessBoardCoord(3, 0),
                locator.locatePiece(new ChessPiece(ChessPieceType.QUEEN,
                ChessSide.WHITE)).iterator().next());
        assertEquals(8,
                locator.locatePiece(new ChessPiece(ChessPieceType.PAWN,
                ChessSide.BLACK)).size());
    }

    @Test
    public void testLocateSide() {
        ChessBoardModel boardModel = new ChessBoardModel();
        boardModel.setInitialPosition();

        PieceLocator locator = new PieceLocator(boardModel);

        assertEquals(16, locator.locatePieces(ChessSide.WHITE).size());
    }
}
