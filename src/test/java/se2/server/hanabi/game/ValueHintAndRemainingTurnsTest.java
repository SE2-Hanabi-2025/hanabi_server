package se2.server.hanabi.game;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ValueHintAndRemainingTurnsTest {
    private ValueHintAndRemainingTurns valueHintAndRemainingTurns;
    private int value = 2;
    private int numTurns = 3;

    @BeforeEach
    public void setup(){
        valueHintAndRemainingTurns = new ValueHintAndRemainingTurns(value, numTurns);
    }

    @Test
    public void testvalueGetter() {
        assertEquals(valueHintAndRemainingTurns.getValue(), value);
    }

    @Test
    public void testNumTurnsGetter() {
        assertEquals(valueHintAndRemainingTurns.getNumTurns(), numTurns);
    }

    @Test
    public void testNumTurnsSetter() {
        int newNumTurns = 5;
        valueHintAndRemainingTurns.setNumTurns(newNumTurns);
        assertEquals(valueHintAndRemainingTurns.getNumTurns(), newNumTurns);
    }
}
