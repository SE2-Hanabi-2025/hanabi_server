package se2.server.hanabi.model;

import org.junit.jupiter.api.Test;


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

    @Test
    void testCardIdsAndDetails() {
        Card.resetNextID(); // Ensure counter is reset
        Deck deck = new Deck();
        Card[] cardsById = new Card[50];

        while (!deck.isEmpty()) {
            Card card = deck.drawCard();
            assertNotNull(card, "Card should not be null");
            assertTrue(card.getId() >= 0 && card.getId() < 50, "Card ID should be between 0 and 49");
            cardsById[card.getId()] = card;
        }

        for (int id = 0; id < 50; id++) {
            assertNotNull(cardsById[id], "Card with ID " + id + " should exist");
            System.out.println("Card ID " + id + ": " + cardsById[id].toString());
        }
    }

    @Test
    void testPrintCardIdsAndDetails() {
        Card.resetNextID(); // Ensure counter is reset
        Deck deck = new Deck();

        while (!deck.isEmpty()) {
            Card card = deck.drawCard();
            assertNotNull(card, "Card should not be null");
            System.out.println("Card ID " + card.getId() + ": " + card.toString());
        }
    }

    @Test
    void testPrintCardIdsAndDetailsInRow() {
        Card.resetNextID(); // Ensure counter is reset
        Deck deck = new Deck();

        StringBuilder cardDetails = new StringBuilder();

        while (!deck.isEmpty()) {
            Card card = deck.drawCard();
            assertNotNull(card, "Card should not be null");
            cardDetails.append("Card ID ").append(card.getId()).append(": ").append(card.toString()).append(" | ");
        }

        System.out.println(cardDetails.toString());
    }

    @Test
    void testPrintCardIdsInOrder() {
        Card.resetNextID(); // Ensure counter is reset
        Deck deck = new Deck();
        Card[] cardsById = new Card[50];

        // Collect all cards by their IDs
        while (!deck.isEmpty()) {
            Card card = deck.drawCard();
            assertNotNull(card, "Card should not be null");
            cardsById[card.getId()] = card;
        }

        // Print cards in order of their IDs
        for (int id = 0; id < 50; id++) {
            assertNotNull(cardsById[id], "Card with ID " + id + " should exist");
            System.out.println("Card ID " + id + ": " + cardsById[id].toString());
        }
    }

}
