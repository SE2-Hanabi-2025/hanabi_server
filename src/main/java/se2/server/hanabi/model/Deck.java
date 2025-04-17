package se2.server.hanabi.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();

        //Hanabi deck structure: 5 colors * (3*1, 2*2, 2*3, 2*4, 1*5)
        for (Color color : Color.values()) {
            addCopies(1,color,3); // 3 ones per color
            addCopies(2,color,2); // 2 twos per color
            addCopies(3,color,2); // 2 threes per color
            addCopies(4,color,2); // 2 fours per color
            addCopies(5,color,1); // 1 five per color
        }
        Collections.shuffle(cards);
    }

    private void addCopies(int value, Color color, int count) {
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
}
