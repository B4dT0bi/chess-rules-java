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

    @Test
    public void testHashCode() {
        ChessPiece wb1 = new ChessPiece(ChessPieceType.BISHOP, ChessSide.WHITE);
        ChessPiece wb2 = new ChessPiece(ChessPieceType.BISHOP, ChessSide.WHITE);
        ChessPiece bp1 = new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK);
        ChessPiece bp2 = new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK);

        Assertions.assertThat(wb1).isEqualTo(wb2);
        Assertions.assertThat(wb1.hashCode()).isEqualTo(wb2.hashCode());
        Assertions.assertThat(bp1).isEqualTo(bp2);
        Assertions.assertThat(bp1.hashCode()).isEqualTo(bp2.hashCode());
        Assertions.assertThat(wb1).isNotEqualTo(bp2);
        Assertions.assertThat(wb1.hashCode()).isNotEqualTo(bp2.hashCode());
    }

    @Test
    public void testToString() {
        ChessPiece wb1 = new ChessPiece(ChessPieceType.BISHOP, ChessSide.WHITE);
        ChessPiece bp1 = new ChessPiece(ChessPieceType.PAWN, ChessSide.BLACK);

        Assertions.assertThat(wb1.toString()).isEqualTo("wb");
        Assertions.assertThat(bp1.toString()).isEqualTo("bp");
    }
}
