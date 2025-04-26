package se2.server.hanabi.gamemanager;

import se2.server.hanabi.gamemanager.actions.PlayCardAction;
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

    public ActionResult playCard(String playerName, int cardIndex) {
        return new PlayCardAction(this, playerName, cardIndex).execute();
    }

    public ActionResult discardCard(String playerName, int handIndex) {
        if (!isCurrentPlayer(playerName)) {
            return ActionResult.invalid("Not your turn.");
        }

        // provisorische Lösung --> Spielregeln nachlesen: kann man Karte ablegen, wenn Hinweise voll sind?
        if (hints >= GameRules.MAX_HINTS) {
            return ActionResult.invalid("Hints are already full.");
        }

        List<Card> hand = hands.get(playerName);
        if (handIndex < 0 || handIndex >= hand.size()) {
            return ActionResult.invalid("Invalid card index.");
        }

        Card discarded = hand.remove(handIndex);
        discardPile.add(discarded);
        hints++;
        drawToHand(playerName);
        advanceTurn();
        return ActionResult.success("Card discarded.");
    }

    public ActionResult giveHint(String playerName, String targetPlayerName, Card.Color color) {
        if (!isCurrentPlayer(playerName)) {
            return ActionResult.invalid("Not your turn.");
        }

        if (hints <= 0) {
            return ActionResult.invalid("No hints left.");
        }

        if (targetPlayerName.equals(playerName)) {
            return ActionResult.invalid("Cannot give a hint to yourself.");
        }

        List<Card> targetHand = hands.get(targetPlayerName);
        for (Card card : targetHand) {
            if (card.getColor() == color) {
                // Hinweis geben
                hints--;
                advanceTurn();
                return ActionResult.success("Hint given successfully.");
            }
        }

        // kann man falsche Hinweise geben?
        return ActionResult.failure("No matching card found in target player's hand.");
    }

    public String getCurrentPlayerName() {
        return players.get(currentPlayerIndex).getName();
    }

    // Helper functions
    private void drawToHand(String playerName) {
        if (!deck.isEmpty()) {
            hands.get(playerName).add(deck.drawCard());
        }
    }

    public void advanceTurn() {
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

    public void drawCardToHand(String playerName) {
        if (!deck.isEmpty()) {
            hands.get(playerName).add(deck.drawCard());
        }
    }

    public void incrementStrikes() {
        strikes++;
    }

    // Getters & Setters
    public List<Player> getPlayers() {
        return players;
    }

    public Map<String, List<Card>> getHands() {
        return hands;
    }

    public Deck getDeck() {
        return deck;
    }

    public Map<Card.Color, Integer> getPlayedCards() {
        return playedCards;
    }

    public List<Card> getDiscardPile() {
        return discardPile;
    }

    public int getHints() {
        return hints;
    }

    public void setHints(int hints) {
        this.hints = hints;
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    public int getCurrentPlayerIndex() {
        return currentPlayerIndex;
    }

    public void setCurrentPlayerIndex(int currentPlayerIndex) {
        this.currentPlayerIndex = currentPlayerIndex;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }
}

