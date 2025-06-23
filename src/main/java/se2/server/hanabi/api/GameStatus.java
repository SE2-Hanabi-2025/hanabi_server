package se2.server.hanabi.api;

import lombok.Getter;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class GameStatus {

    @Getter
    private final List<Player> players;
    @Getter
    private final List<Integer> playerCardIds;
    private final Map<Integer, List<Card>> visibleHands;
    @Getter
    private final Map<Card.Color, Integer> playedCards;
    @Getter
    private final List<Card> discardPile;
    private final int numRemaningCards;
    @Getter
    private final Map<Integer, Card.Color> cardsShowingColorHints;
    @Getter
    private final Map<Integer, Integer> cardsShowingValueHints;
    @Getter
    private final int numRemainingHintTokens;
    @Getter
    private final int strikes;
    @Getter
    private final boolean gameOver;
    @Getter
    private final boolean gameLost;
    @Getter
    private final int currentScore;
    @Getter
    private final int currentPlayerId;

    @Getter
    private List<Card> ownHand = List.of();


    public GameStatus(
            List<Player> players,
            List<Integer> playerCardIds,
            Map<Integer, List<Card>> visibleHands,
            Map<Card.Color, Integer> playedCards,
            List<Card> discardPile,
            int numRemaningCards,
            Map<Integer, Card.Color> cardsShowingColorHints,
            Map<Integer, Integer> cardsShowingValueHints,
            int numRemainingHintTokens,
            int strikes,
            boolean gameOver,
            boolean gameLost,
            int currentScore,
            int currentPlayerId,
            List<Card> ownHand)
    {
        this.players = players;
        this.playerCardIds = playerCardIds;
        this.visibleHands = visibleHands;
        this.playedCards = playedCards;
        this.discardPile = discardPile;
        this.numRemaningCards = numRemaningCards;
        this.cardsShowingColorHints = cardsShowingColorHints;
        this.cardsShowingValueHints = cardsShowingValueHints;
        this.numRemainingHintTokens = numRemainingHintTokens;
        this.strikes = strikes;
        this.gameOver = gameOver;
        this.gameLost = gameLost;
        this.currentScore = currentScore;
        this.currentPlayerId = currentPlayerId;
        this.ownHand = ownHand;
    }

    public Map<Integer, List<Card>> getVisibleHands() {
        // Convert the keys of the map from String to Integer
        Map<Integer, List<Card>> convertedHands = new HashMap<>();
        for (Map.Entry<Integer, List<Card>> entry : visibleHands.entrySet()) {
            convertedHands.put((entry.getKey()), entry.getValue());
        }
        return convertedHands;
    }

    public int getNumRemainingCards() {
        return numRemaningCards;
    }

}
