package se2.server.hanabi;

import org.junit.jupiter.api.Test;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Color;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    void testCardValueAndToString() {
        Card card = new Card(3, Color.GREEN);
        assertEquals(3, card.getValue());
        assertEquals("Card{value=3 color=GREEN}", card.toString());
    }
    
}
