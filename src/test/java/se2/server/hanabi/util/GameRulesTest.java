package se2.server.hanabi.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

public class GameRulesTest {
    @ParameterizedTest
    @CsvSource({
        "2,5",
        "3,5",
        "4,4",
        "5,4"
    })
    void testGetInitialHandSize(int playerCount, int expectedHandSize) {
        assertEquals(expectedHandSize, GameRules.getInitialHandSize(playerCount));
    }

    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5})
    void testIsPlayerCountValid(int count) {
        assertTrue(GameRules.isPlayerCountValid(count));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 6, 0, -1})
    void isPlayerCountNotValid(int count) {
        assertFalse(GameRules.isPlayerCountValid(count));
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    void testIsValidCardValue(int value) {
        assertTrue(GameRules.isValidCardValue(value));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 6, -1})
    void isCardValueNotValid(int value) {
        assertFalse(GameRules.isValidCardValue(value));
    }
}
