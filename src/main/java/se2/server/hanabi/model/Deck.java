package se2.server.hanabi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        Card.Color[] colors = Card.Color.values();

        //Hanabi deck structure: 5 colors * (3*1, 2*2, 2*3, 2*4, 1*5)
        for (Card.Color color : colors) {
            addCopies(1, 3, color); // 3 ones per color
            addCopies(2, 2, color); // 2 twos per color
            addCopies(3, 2, color); // 2 threes per color
            addCopies(4, 2, color); // 2 fours per color
            addCopies(5, 1, color); // 1 five per color
        }
        Collections.shuffle(cards);
    }

    private void addCopies(int value, int count, Card.Color color) {
        for (int i = 0; i < count; i++) {
            cards.add(new Card(value, color));
        }
    }

    public Card drawCard() {
        return cards.isEmpty() ? null : cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
    
    /**
     * Get the number of cards remaining in the deck
     * @return number of remaining cards
     */
    public int getRemainingCards() {
        return cards.size();
    }
}
