package se2.server.hanabi.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @BeforeEach
    void resetCardIDs() {
        Card.resetNextID();
    }

    @Test
    void testCardValueAndColorToString() {
        Card card = new Card(3, Card.Color.RED);
        assertEquals(3, card.getValue());
        assertEquals("Card{value=3, color=RED, id=0}", card.toString());
    }
    
    @Test
    void testCardToStringForMultipleCards() {
        Card.resetNextID();

        Card card1 = new Card(1, Card.Color.BLUE);
        Card card2 = new Card(2, Card.Color.GREEN);
        Card card3 = new Card(5, Card.Color.RED);

        assertEquals("Card{value=1, color=BLUE, id=0}", card1.toString(), "Card 1 toString output is incorrect");
        assertEquals("Card{value=2, color=GREEN, id=1}", card2.toString(), "Card 2 toString output is incorrect");
        assertEquals("Card{value=5, color=RED, id=2}", card3.toString(), "Card 3 toString output is incorrect");
    }

}
