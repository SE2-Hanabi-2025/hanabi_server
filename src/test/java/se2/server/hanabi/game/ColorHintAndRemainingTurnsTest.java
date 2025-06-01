package se2.server.hanabi.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se2.server.hanabi.model.Card;

class ColorHintAndRemainingTurnsTest {
    private ColorHintAndRemainingTurns colorHintAndRemainingTurns;
    private Card.Color color = Card.Color.BLUE;
    private int numTurns = 3;

    @BeforeEach
    public void setup(){
        colorHintAndRemainingTurns = new ColorHintAndRemainingTurns(color, numTurns);
    }

    @Test
    public void testColorGetter() {
        assertEquals(colorHintAndRemainingTurns.getColor(), color);
    }

    @Test
    public void testNumTurnsGetter() {
        assertEquals(colorHintAndRemainingTurns.getNumTurns(), numTurns);
    }

    @Test
    public void testNumTurnsSetter() {
        int newNumTurns = 5;
        colorHintAndRemainingTurns.setNumTurns(newNumTurns);
        assertEquals(colorHintAndRemainingTurns.getNumTurns(), newNumTurns);
    }
}
