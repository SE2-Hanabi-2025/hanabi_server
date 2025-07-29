package se2.server.hanabi.game;

import lombok.Getter;
import lombok.Setter;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.util.GameRules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates the state of a Hanabi game
 */
public class GameState {
    @Getter
    private final List<Player> players;
    @Getter
    private final Map<Integer, List<Card>> hands = new HashMap<>();
    @Getter
    private final Deck deck;
    @Getter
    private final Map<Card.Color, Integer> playedCards = new HashMap<>();
    @Getter
    private final List<Card> discardPile = new ArrayList<>();
    @Getter
    private final int numTurnsHintsLast;
    @Getter
    private final Map<Integer, ColorHintAndRemainingTurns> cardsShowingColorHintsAndRemainingTurns = new HashMap<>();
    @Getter
    private final Map<Integer, ValueHintAndRemainingTurns> cardsShowingValueHintsAndRemainingTurns = new HashMap<>();
    @Getter
    private int numRemainingHintTokens = GameRules.MAX_HINT_TOKENS;
    @Setter
    @Getter
    private int strikes = 0;
    private int currentPlayerIndex = 0;
    @Getter
    private boolean gameOver = false;
    @Setter
    @Getter
    private boolean gameLost = false;
    @Setter
    @Getter
    private int finalTurnsRemaining = -1;
    private final GameLogger logger;
    private int lastStrikeTurn = -1;
    private int turnCounter = 0;

    /**
     * Constructor for the game state
     * @param players the list of players
     * @param numTurnsHintsLast the number of turns before hints will disappear, set to -1 for persistant hints
     * @param logger the game logger for logging game events
     */
    public GameState(List<Player> players, int numTurnsHintsLast, GameLogger logger) {
        this.players = players;
        this.numTurnsHintsLast = numTurnsHintsLast;
        this.logger = logger;
        this.deck = new Deck();
        
        initializePlayedCards();
    }

    /**
     * Constructor for the game state
     * @param players the list of players
     * @param logger the game logger for logging game events
     */
    public GameState(List<Player> players, GameLogger logger) {
        this(players, GameRules.TURNS_HINTS_LAST_DEFAULT, logger);
    }

    private void initializePlayedCards() {
        for (Card.Color color : Card.Color.values()) {
            playedCards.put(color, 0);
        }
    }

    public void dealInitialCards() {
        int handSize = GameRules.getInitialHandSize(players.size());
        logger.info("Dealing " + handSize + " cards per player");
        
        for (Player player : players) {
            List<Card> hand = new ArrayList<>();
            for (int i = 0; i < handSize; i++) {
                Card card = deck.drawCard();
                hand.add(card);
            }
            hands.put(player.getId(), hand);
        }
    }
    
    public boolean isCurrentPlayer(int playerId) {
        return players.get(currentPlayerIndex).getId() == playerId;
    }

    public boolean isActionValid(int playerId) {
        return !gameOver && isCurrentPlayer(playerId);
    }

    public boolean isValidCardIndex(int playerId, int cardIndex) {
        List<Card> hand = hands.get(playerId);
        return hand != null && cardIndex >= 0 && cardIndex < hand.size();
    }
    
    public boolean playerExists(int playerId) {
        return players.stream().anyMatch(p -> p.getId() == playerId);
    }

    public int getCurrentPlayerId() {
        return players.get(currentPlayerIndex).getId();
    }

    public Map<Integer, Card.Color> getCardsShowingColorHints() {
        Map<Integer, Card.Color> cardsShowingColorHints = new HashMap<>();
        cardsShowingColorHintsAndRemainingTurns.forEach((cardId, colorHintAndRemainingTurns) -> 
            cardsShowingColorHints.put(cardId, colorHintAndRemainingTurns.getColor())
        );
        return cardsShowingColorHints;
    }

    public Map<Integer, Integer> getCardsShowingValueHints() {
        Map<Integer, Integer> cardsShowingValueHints = new HashMap<>();
        cardsShowingValueHintsAndRemainingTurns.forEach((cardId, ValueHintAndRemainingTurns) -> 
            cardsShowingValueHints.put(cardId, ValueHintAndRemainingTurns.getValue())
        );
        return cardsShowingValueHints;
    }

    public boolean advanceTurn() {
        if (gameOver) {
            return false;
        }
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        turnCounter++;
        logger.info("Turn advances to " + getCurrentPlayerId() + ", turnCounter=" + turnCounter);

        removeExpiredShownHints();
        decrementHintsRemainingTurns();
        
        if (finalTurnsRemaining > 0) {
            finalTurnsRemaining--;
            logger.info("Final round: " + finalTurnsRemaining + " turns remaining.");
        }
        
        return true;
    }
    
    public boolean checkEndCondition() {
        if (strikes >= GameRules.MAX_STRIKES) {
            gameOver = true;
            gameLost = true;
            logger.error("Game over: maximum strikes reached (" + strikes + ")");
            return true;
        }

        gameLost = false;
        boolean isPerfect = playedCards.values().stream().allMatch(v -> v == GameRules.MAX_CARD_VALUE);

        if (isPerfect) {
            gameOver = true;
            logger.info("Game completed perfectly!");
            return true;
        }
        
        if (finalTurnsRemaining == 0) {
            gameOver = true;
            logger.info("Game over: final turns reached");
            return true;
        }
        
        return false;
    }
    
    public int getCurrentScore() {
        if (gameLost) {
            return 0;
        } else {
            return playedCards.values().stream().mapToInt(Integer::intValue).sum();
        }
        
    }
    
    public void incrementStrikes() {
        logger.info("Incrementing strikes. Current strikes: " + strikes);
        strikes++;
        logger.warn("Strike count increased to " + strikes + " out of " + GameRules.MAX_STRIKES);
    }
    
    public List<Integer> getPlayerCardIds(int playerId) {
        List<Integer> cardIds = new ArrayList<Integer>();
        for (Card card : hands.get(playerId)) {
            cardIds.add(card.getId());
        }
        return cardIds;
    }

    public Map<Integer, List<Card>> getVisibleHands(int viewerId) {
        Map<Integer, List<Card>> copy = new HashMap<>();
        for (Map.Entry<Integer, List<Card>> entry : hands.entrySet()) {
            if (entry.getKey() != viewerId) {
                copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return copy;
    }

    public void setNumRemainingHintTokens(int numRemainingHintTokens) {
        this.numRemainingHintTokens = Math.min(numRemainingHintTokens, GameRules.MAX_HINT_TOKENS);
        logger.info("Hint tokens updated to " + this.numRemainingHintTokens + " out of " + GameRules.MAX_HINT_TOKENS);
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (gameOver) {
            logger.info("Game is now marked as over.");
        }
    }

    public void removeCardFromShownHints(int cardId) {
        cardsShowingColorHintsAndRemainingTurns.remove(cardId);
        cardsShowingValueHintsAndRemainingTurns.remove(cardId);
    }

    public void removeExpiredShownHints() {
        List<Integer> colorHintsToBeRemoved = new ArrayList<Integer>();
        cardsShowingColorHintsAndRemainingTurns.forEach((cardId, colorHintAndRemainingTurns) -> {
            if (colorHintAndRemainingTurns.getNumTurns()==0) {
                colorHintsToBeRemoved.add(cardId);
            }
        });
        colorHintsToBeRemoved.forEach(cardsShowingColorHintsAndRemainingTurns::remove);
        
        List<Integer> valueHintsToBeRemoved = new ArrayList<Integer>();
        cardsShowingValueHintsAndRemainingTurns.forEach((cardId, valueHintAndRemainingTurns) -> {
            if (valueHintAndRemainingTurns.getNumTurns()==0) {
                valueHintsToBeRemoved.add(cardId);
            }
        });
        valueHintsToBeRemoved.forEach(cardsShowingValueHintsAndRemainingTurns::remove);

        int numHintsRemoved = colorHintsToBeRemoved.size()+valueHintsToBeRemoved.size();
        logger.info(numHintsRemoved+" hints removed.");
    }

    public void decrementHintsRemainingTurns() {
        cardsShowingColorHintsAndRemainingTurns.forEach((cardId, colorHintAndRemainingTurns) -> {
            colorHintAndRemainingTurns.setNumTurns(colorHintAndRemainingTurns.getNumTurns() -1);
        });
        cardsShowingValueHintsAndRemainingTurns.forEach((cardId, valueHintAndRemainingTurns) -> {
            valueHintAndRemainingTurns.setNumTurns(valueHintAndRemainingTurns.getNumTurns() -1);
        });

        int numHintsShown = cardsShowingColorHintsAndRemainingTurns.size() + cardsShowingValueHintsAndRemainingTurns.size();
        logger.info("Remaining turns for shown hints decremented. "+numHintsShown+" hints are shown");
    }

    public int getCurrentTurnNumber() {
        return currentPlayerIndex;
    }
    public int getLastStrikeTurn() {
        return lastStrikeTurn;
    }
    public void setLastStrikeTurn(int turn) {
        lastStrikeTurn = turn;
    }

    public int getTurnCounter() {
        return turnCounter;
    }
}