package se2.server.hanabi.api;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GameStatus {

    private final List<Player> players;
    private final List<Integer> playersHand;
    private final Map<Integer, List<Card>> visibleHands;
    private final Map<Card.Color, Integer> playedCards;
    private final List<Card> discardPile;
    private final int numRemainingCard;
    private final Map<Integer, Object> shownHints; // Simplified for now
    private final int hintTokens;
    private final int strikes;
    private final boolean gameOver;
    private final int currentPlayer;    public GameStatus(List<Player> players, List<Integer> playersHand, Map<Integer, List<Card>> visibleHands, Map<Card.Color, Integer> playedCards, List<Card> discardPile, int numRemainingCard, Map<Integer, Object> shownHints, int hintTokens, int strikes, boolean gameOver, int currentPlayer) {
        this.players = players;
        this.playersHand = playersHand;
        this.visibleHands = visibleHands;
        this.playedCards = playedCards;
        this.discardPile = discardPile;
        this.numRemainingCard = numRemainingCard;
        this.shownHints = shownHints;
        this.hintTokens = hintTokens;
        this.strikes = strikes;
        this.gameOver = gameOver;
        this.currentPlayer = currentPlayer;
    }    
    
    public List<Player> getPlayers() {
        return players;
    }

    public List<Integer> getPlayersHand() {
        return playersHand;
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

    public int getNumRemainingCard() {
        return numRemainingCard;
    }

    public Map<Integer, Object> getShownHints() {
        return shownHints;
    }

    public int getHintTokens() {
        return hintTokens;
    }

    public int getStrikes() {
        return strikes;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCurrentPlayerId() {
        return currentPlayer;
    }
}
