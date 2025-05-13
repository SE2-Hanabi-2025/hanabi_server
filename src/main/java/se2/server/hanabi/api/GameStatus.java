package se2.server.hanabi.api;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GameStatus {

    private final List<Player> players;
    private final Map<Integer, List<Card>> visibleHands;
    private final Map<Card.Color, Integer> playedCards;
    private final List<Card> discardPile;
    private final int hints;
    private final int strikes;
    private final boolean gameOver;
    private final String currentPlayer;

    public GameStatus(List<Player> players, Map<Integer, List<Card>> visibleHands, Map<Card.Color, Integer> playedCards, List<Card> discardPile, int hints, int strikes, boolean gameOver, String currentPlayer) {
        this.players = players;
        this.visibleHands = visibleHands;
        this.playedCards = playedCards;
        this.discardPile = discardPile;
        this.hints = hints;
        this.strikes = strikes;
        this.gameOver = gameOver;
        this.currentPlayer = currentPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public Map<Integer, List<Card>> getVisibleHands() {
        // Convert the keys of the map from String to Integer
        Map<Integer, List<Card>> convertedHands = new HashMap<>();
        for (Map.Entry<Integer, List<Card>> entry : visibleHands.entrySet()) {
            convertedHands.put((entry.getKey()), entry.getValue());
        }
        return convertedHands;
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

    public int getStrikes() {
        return strikes;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCurrentPlayerId() {
        // Assuming `currentPlayer` is the ID of the current player as a String
        return Integer.parseInt(currentPlayer);
    }
}
