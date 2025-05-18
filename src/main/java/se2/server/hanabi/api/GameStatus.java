package se2.server.hanabi.api;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GameStatus {

    private final List<Player> players;
    private final List<Integer> playerCardIds;
    private final Map<Integer, List<Card>> visibleHands;
    private final Map<Card.Color, Integer> playedCards;
    private final List<Card> discardPile;
    private final int numRemaningCards;
    private final int numRemainingHintTokens;
    private final int strikes;
    private final boolean gameOver;
    private final int currentPlayerId;

    public GameStatus(
        List<Player> players, 
        List<Integer> playerCardIds,
        Map<Integer, List<Card>> visibleHands, 
        Map<Card.Color, Integer> playedCards, 
        List<Card> discardPile, 
        int numRemaningCards,
        int hints, 
        int strikes, 
        boolean gameOver, 
        int currentPlayerId
        ) {
        this.players = players;
        this.playerCardIds = playerCardIds;
        this.visibleHands = visibleHands;
        this.playedCards = playedCards;
        this.discardPile = discardPile;
        this.numRemaningCards = numRemaningCards;
        this.numRemainingHintTokens = hints;
        this.strikes = strikes;
        this.gameOver = gameOver;
        this.currentPlayerId = currentPlayerId;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<Integer> getPlayerCardIds() {
        return playerCardIds;
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

    public int getNumRemainingCards() {
        return numRemaningCards;
    }

    public int getNumRemainingHintTokens() {
        return numRemainingHintTokens;
    }

    public int getStrikes() {
        return strikes;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public int getCurrentPlayerId() {
        return currentPlayerId;
    }
}
