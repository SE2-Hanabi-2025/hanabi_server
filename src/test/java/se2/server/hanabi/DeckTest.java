package se2.server.hanabi;

import org.junit.jupiter.api.Test;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;

import static org.junit.jupiter.api.Assertions.*;

public class DeckTest {

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
    void testIsEmpty() {
        Deck deck = new Deck();
        for (int i = 0; i < 50; i++) {
            deck.drawCard();
        }
        assertTrue(deck.isEmpty());
    }
}
