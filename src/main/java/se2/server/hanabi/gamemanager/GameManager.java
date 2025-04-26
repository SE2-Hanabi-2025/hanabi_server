package se2.server.hanabi.gamemanager;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.rules.GameRules;
import se2.server.hanabi.services.ActionResult;

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

    public ActionResult playCard(String playerName, int handIndex) {
        if (!isCurrentPlayer(playerName)) {
            return ActionResult.invalid("Not your turn.");
        }

        List<Card> hand = hands.get(playerName);
        if (handIndex < 0 || handIndex >= hand.size()) {
            return ActionResult.invalid("Invalid card index.");
        }

        Card played = hand.remove(handIndex);
        int expectedValue = playedCards.get(played.getColor()) + 1;

        if (played.getValue() == expectedValue) {
            playedCards.put(played.getColor(), expectedValue);
            drawToHand(playerName);
            checkEndCondition();
            advanceTurn();
            return ActionResult.success("Card played successfully.");
        } else {
            discardPile.add(played);
            strikes++;
            drawToHand(playerName);
            if (strikes >= GameRules.MAX_STRIKES) {
                gameOver = true;
                return ActionResult.failure("Wrong card. Game over!");
            }
            advanceTurn();
            return ActionResult.failure("Wrong card. Strike!");
        }
    }

    // Helper functions
    private void drawToHand(String playerName) {
        if (!deck.isEmpty()) {
            hands.get(playerName).add(deck.drawCard());
        }
    }

    private void advanceTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    private boolean isCurrentPlayer(String playerName) {
        return players.get(currentPlayerIndex).getName().equals(playerName);
    }

    private void checkEndCondition() {
        if (deck.isEmpty()) {
            // z. B. letzte Runde einläuten oder sofort beenden
            gameOver = true; // oder: setze letzte Rundenlogik
        }

        boolean isPerfect = playedCards.values().stream()
                .allMatch(v -> v == GameRules.MAX_CARD_VALUE);
        if (isPerfect) {
            gameOver = true;
        }
    }
}

