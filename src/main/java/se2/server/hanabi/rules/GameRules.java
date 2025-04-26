package se2.server.hanabi.rules;

import se2.server.hanabi.model.Card;

public class GameRules {

    // Anzahl Spieler
    public static final int MIN_PLAYERS = 2;
    public static final int MAX_PLAYERS = 5;

    // Hinweise & Strikes
    public static final int MAX_HINTS = 8;
    public static final int MAX_STRIKES = 3;

    // Kartenwerte
    public static final int MIN_CARD_VALUE = 1;
    public static final int MAX_CARD_VALUE = 5;

    // Handkarten
    public static final int HAND_SIZE_SMALL_GROUP = 5; // 2–3 Spieler
    public static final int HAND_SIZE_LARGE_GROUP = 4; // 4–5 Spieler

    // Maximalpunkte
    public static final int MAX_SCORE = Card.Color.values().length * MAX_CARD_VALUE;

    private GameRules() {
        // Utility class → kein Konstruktor
    }

    public static int getInitialHandSize(int playerCount) {
        return playerCount <= 3 ? HAND_SIZE_SMALL_GROUP : HAND_SIZE_LARGE_GROUP;
    }

    public static boolean isValidCardValue(int value) {
        return value >= MIN_CARD_VALUE && value <= MAX_CARD_VALUE;
    }

    public static boolean isPlayerCountValid(int count) {
        return count >= MIN_PLAYERS && count <= MAX_PLAYERS;
    }
}
