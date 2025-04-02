package se2.server.hanabi.CardLogic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Deck {
    private List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        for (int i = 0; i < 5; i++) {  // 5 sets
            for (int j = 1; j <= 5; j++) {
                cards.add(new Card(j));
            }
        }
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        return cards.isEmpty() ? null : cards.remove(0);
    }

    public boolean isEmpty() {
        return cards.isEmpty();
    }
}
