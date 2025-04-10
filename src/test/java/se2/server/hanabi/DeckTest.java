package se2.server.hanabi;

import org.junit.jupiter.api.Test;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Color;
import se2.server.hanabi.model.Deck;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;

public class DeckTest {
    Deck deck;

    @BeforeEach
    public void initDeck() {
        deck = new Deck();
    }

    @Test
    void testDeckInitializationAndDraw() {
        Deck deck = new Deck();
        int drawnCount = 0;
        while (!deck.isEmpty()) {
            Card card = deck.drawCard();
            assertNotNull(card);
            assertTrue(card.getValue() >= 1 && card.getValue() <= 5);
            drawnCount++;
        }
        assertEquals(50, drawnCount); // 5 colors × 10 cards each
        assertNull(deck.drawCard());
    }

    @Test
    void testDeckValueColorCompostion() {
        int drawnCards[][] = new int[Color.values().length][5];
        while (!deck.isEmpty()) {
            Card card = deck.drawCard();
            assertNotNull(card);
            drawnCards[card.getColor().ordinal()][card.getValue()-1] += 1;
        }
        for (Color color : Color.values()) {
            assertEquals(drawnCards[color.ordinal()][0], 3); // check each color has 3 ones drawn
            assertEquals(drawnCards[color.ordinal()][1], 2); // check each color has 2 twos drawn
            assertEquals(drawnCards[color.ordinal()][2], 2); // check each color has 2 threes drawn
            assertEquals(drawnCards[color.ordinal()][3], 2); // check each color has 2 fours drawn
            assertEquals(drawnCards[color.ordinal()][4], 1); // check each color has 1 fives drawn

        }
    }

    @Test
    void testIsEmpty() {
        Deck deck = new Deck();
        for (int i = 0; i < 50; i++) {
            deck.drawCard();
        }
        assertTrue(deck.isEmpty());
    }
}
