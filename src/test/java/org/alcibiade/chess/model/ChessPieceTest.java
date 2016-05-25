package org.alcibiade.chess.model;

import org.assertj.core.api.Assertions;
import org.junit.Test;

/**
 * Test individual model operations.
 */
public class ChessPieceTest {

    @Test
    public void testCharacterRepresentation() {
        Assertions.assertThat(new ChessPiece(ChessPieceType.KING, ChessSide.WHITE).getAsSingleCharacter()).isEqualTo('K');
        Assertions.assertThat(new ChessPiece(ChessPieceType.KNIGHT, ChessSide.WHITE).getAsSingleCharacter()).isEqualTo('N');
        Assertions.assertThat(new ChessPiece(ChessPieceType.KNIGHT, ChessSide.BLACK).getAsSingleCharacter()).isEqualTo('n');
    }

    @Test
    public void testName() {
        Assertions.assertThat(new ChessPiece(ChessPieceType.BISHOP, ChessSide.WHITE).getInitials()).isEqualTo("wb");
        Assertions.assertThat(new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK).getInitials()).isEqualTo("bp");
    }
}
