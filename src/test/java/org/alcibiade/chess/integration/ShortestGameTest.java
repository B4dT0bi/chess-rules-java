package org.alcibiade.chess.integration;

import org.alcibiade.chess.model.ChessGameStatus;
import org.alcibiade.chess.model.ChessMovePath;
import org.alcibiade.chess.model.ChessPosition;
import org.alcibiade.chess.model.IllegalMoveException;
import org.alcibiade.chess.model.PgnMoveException;
import org.alcibiade.chess.rules.ChessHelper;
import org.alcibiade.chess.rules.ChessRules;
import org.alcibiade.chess.rules.PgnMarshaller;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"testContext.xml"})
public class ShortestGameTest {
    
    @Autowired
    private ChessRules chessRules;
    
    @Autowired
    private PgnMarshaller pgnMarshaller;
    
    @Test
    public void testGame() throws PgnMoveException, IllegalMoveException {
        String[] history = {"f3", "e5", "g4", "Qh4"};
        ChessPosition position = chessRules.getInitialPosition();
        
        for (String pgnMove : history) {
            ChessMovePath path = pgnMarshaller.convertPgnToMove(position, pgnMove);
            position = ChessHelper.applyMoveAndSwitch(chessRules, position, path);
        }
        
        Assertions.assertThat(chessRules.getStatus(position)).isEqualTo(ChessGameStatus.BLACKWON);
        Assertions.assertThat(chessRules.getAvailableMoves(position)).isEmpty();
    }
}
