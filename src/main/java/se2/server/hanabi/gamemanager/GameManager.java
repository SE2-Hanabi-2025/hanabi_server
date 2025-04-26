package se2.server.hanabi.gamemanager;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.rules.GameRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameManager {
    private final List<Player> players;
    private final Map<String, List<Card>> hands = new HashMap<>();
    private final Deck deck = new Deck();
    private final Map<Card.Color, Integer> playedCards = new HashMap<>();
    private final List<Card> discardPile = new ArrayList<>();
    private int hints = GameRules.MAX_HINTS;
    private int strikes = 0;
    private int currentPlayerIndex = 0;
    private boolean gameOver = false;

    public GameManager(List<Player> players) {
        if (!GameRules.isPlayerCountValid(players.size())) {
            throw new IllegalArgumentException("Invalid number of players");
        }
        this.players = players;

        // Initialisiere gespielte Karten
        for (Card.Color color : Card.Color.values()) {
            playedCards.put(color, 0);
        }

        int handSize = GameRules.getInitialHandSize(players.size());
        for (Player player : players) {
            List<Card> hand = new ArrayList<>();
            for (int i = 0; i < handSize; i++) {
                hand.add(deck.drawCard());
            }
            hands.put(player.getName(), hand);
        }
    }
}

