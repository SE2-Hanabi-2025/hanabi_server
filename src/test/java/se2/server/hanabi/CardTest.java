package se2.server.hanabi;

import org.junit.jupiter.api.Test;

import se2.server.hanabi.model.Card;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    void testCardValueAndColorToString() {
        Card card = new Card(3, Card.Color.RED);
        assertEquals(3, card.getValue());
        assertEquals("Card{value=3, color=RED}", card.toString());
    }
    
}
