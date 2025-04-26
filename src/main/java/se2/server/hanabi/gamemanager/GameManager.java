package se2.server.hanabi.gamemanager;

import se2.server.hanabi.api.GameStatus;
import se2.server.hanabi.gamemanager.actions.DiscardCardAction;
import se2.server.hanabi.gamemanager.actions.HintAction;
import se2.server.hanabi.gamemanager.actions.PlayCardAction;
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

    public ActionResult playCard(String playerName, int cardIndex) {
        return new PlayCardAction(this, playerName, cardIndex).execute();
    }

    public ActionResult discardCard(String playerName, int cardIndex) {
        return new DiscardCardAction(this, playerName, cardIndex).execute();
    }

    public ActionResult giveHint(String fromPlayer, String toPlayer, HintType type, Object value) {
        return new HintAction(this, fromPlayer, toPlayer, type, value).execute();
    }

    public String getCurrentPlayerName() {
        return players.get(currentPlayerIndex).getName();
    }

    // DTOs GameStatus

    public GameStatus getStatusFor(String viewer) {
        return new GameStatus(
                players,
                getVisibleHands(viewer),
                playedCards,
                discardPile,
                hints,
                strikes,
                gameOver,
                getCurrentPlayerName()
        );
    }

    public Map<String, List<Card>> getVisibleHands(String viewer) {
        Map<String, List<Card>> copy = new HashMap<>();
        for (Map.Entry<String, List<Card>> entry : hands.entrySet()) {
            if (!entry.getKey().equals(viewer)) {
                copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return copy;
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

