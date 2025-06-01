package se2.server.hanabi.game;

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
    private final List<Player> players;
    private final Map<Integer, List<Card>> hands = new HashMap<>();
    private final Deck deck;
    private final Map<Card.Color, Integer> playedCards = new HashMap<>();
    private final List<Card> discardPile = new ArrayList<>();
    private final int numTurnsHintsLast;
    private final Map<Integer, ColorHintAndRemainingTurns> cardsShowingColorHintsAndRemainingTurns = new HashMap<>();
    private final Map<Integer, ValueHintAndRemainingTurns> cardsShowingValueHintsAndRemainingTurns = new HashMap<>();
    private int numRemainingHintTokens = GameRules.MAX_HINT_TOKENS;
    private int strikes = 0;
    private int currentPlayerIndex = 0;
    private boolean gameOver = false;
    private int finalTurnsRemaining = -1;
    private final GameLogger logger;

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
    
    /**
     * Initialize the played cards map with all colors set to 0
     */
    private void initializePlayedCards() {
        for (Card.Color color : Card.Color.values()) {
            playedCards.put(color, 0);
        }
    }
    
    /**
     * Deal initial cards to all players based on the number of players
     */
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
    
    /**
     * Checks if a player is the current player
     * @param playerId the ID of the player to check
     * @return true if the player is the current player, false otherwise
     */
    public boolean isCurrentPlayer(int playerId) {
        return players.get(currentPlayerIndex).getId() == playerId;
    }
    
    /**
     * Checks if an action is valid
     * @param playerId the ID of the player attempting the action
     * @return true if the action is valid, false otherwise
     */
    public boolean isActionValid(int playerId) {
        return !gameOver && isCurrentPlayer(playerId);
    }
    
    /**
     * Check if a card index is valid for a player's hand
     */
    public boolean isValidCardIndex(int playerId, int cardIndex) {
        List<Card> hand = hands.get(playerId);
        return hand != null && cardIndex >= 0 && cardIndex < hand.size();
    }
    
    /**
     * Check if a player exists in the game
     * @param playerId the ID of the player to check
     * @return true if the player exists, false otherwise
     */
    public boolean playerExists(int playerId) {
        return players.stream().anyMatch(p -> p.getId() == playerId);
    }

    /**
     * Get the current player's ID
     * @return the ID of the current player
     */
    public int getCurrentPlayerId() {
        return players.get(currentPlayerIndex).getId();
    }

    /**
     * Get the number of turns that a hint will last until it disapears
     * @return the number of turns that a hint will last until it disapears
     */
    public int getNumTurnsHintsLast() {
        return numTurnsHintsLast;
    }

    /**
     * Get the map of card IDs along with their corresponding color hints and the number of turns until they are removed
     * @return map of card IDs along with their corresponding color hints and the number of turns until they are removed
     */
    public Map<Integer, ColorHintAndRemainingTurns> getCardsShowingColorHintsAndRemainingTurns() {
        return cardsShowingColorHintsAndRemainingTurns;
    }

    /**
     * Get the map of card IDs along with their corresponding value hints and the number of turns until they are removed
     * @return map of card IDs along with their corresponding value hints and the number of turns until they are removed
     */
    public Map<Integer, ValueHintAndRemainingTurns> getCardsShowingValueHintsAndRemainingTurns() {
        return cardsShowingValueHintsAndRemainingTurns;
    }

    /**
     * Get a map of card IDs and their corresponding color hints
     * @return map of card IDs and their corresponding color hints
     */
    public Map<Integer, Card.Color> getCardsShowingColorHints() {
        Map<Integer, Card.Color> cardsShowingColorHints = new HashMap<>();
        cardsShowingColorHintsAndRemainingTurns.forEach((cardId, colorHintAndRemainingTurns) -> 
            cardsShowingColorHints.put(cardId, colorHintAndRemainingTurns.getColor())
        );
        return cardsShowingColorHints;
    }

    /**
     * Get a map of card IDs and their corresponding value hints
     * @return map of card IDs and their corresponding color hints
     */
    public Map<Integer, Integer> getCardsShowingValueHints() {
        Map<Integer, Integer> cardsShowingValueHints = new HashMap<>();
        cardsShowingValueHintsAndRemainingTurns.forEach((cardId, ValueHintAndRemainingTurns) -> 
            cardsShowingValueHints.put(cardId, ValueHintAndRemainingTurns.getValue())
        );
        return cardsShowingValueHints;
    }

    /**
     * Advances to the next player's turn
     * @return true if the turn was advanced, false if the game is over
     */
    public boolean advanceTurn() {
        if (gameOver) {
            return false;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        logger.info("Turn advances to " + getCurrentPlayerId());

        removeExpiredShownHints();
        decrementHintsRemainingTurns();
        
        // Update final turns counter if we're in final rounds
        if (finalTurnsRemaining > 0) {
            finalTurnsRemaining--;
            logger.info("Final round: " + finalTurnsRemaining + " turns remaining.");
        }
        
        return true;
    }
    
    /**
     * Checks if the game has ended
     * @return true if any end condition is met, false otherwise
     */
    public boolean checkEndCondition() {
        if (strikes >= GameRules.MAX_STRIKES) {
            gameOver = true;
            logger.error("Game over: maximum strikes reached (" + strikes + ")");
            return true;
        }

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
    
    /**
     * Get the current score
     * @return current score as sum of all played cards' values
     */
    public int getCurrentScore() {
        return playedCards.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Increment the strikes counter
     */
    public void incrementStrikes() {
        logger.info("Incrementing strikes. Current strikes: " + strikes);
        strikes++;
        logger.warn("Strike count increased to " + strikes + " out of " + GameRules.MAX_STRIKES);
    }
    
    /**
     *  Returns the cardIds of the given player
     * @param playerId ID of the players whose cardIds will be returned
     * @return List of card IDs
     */
    public List<Integer> getPlayerCardIds(int playerId) {
        List<Integer> cardIds = new ArrayList<Integer>();
        for (Card card : hands.get(playerId)) {
            cardIds.add(card.getId());
        }
        return cardIds;
    }

    /**
     * Get all hands except the specified player's
     * @param viewerId ID of the player who should not see their own hand
     * @return Map of player IDs to their hand of cards
     */
    public Map<Integer, List<Card>> getVisibleHands(int viewerId) {
        Map<Integer, List<Card>> copy = new HashMap<>();
        for (Map.Entry<Integer, List<Card>> entry : hands.entrySet()) {
            if (entry.getKey() != viewerId) {
                copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return copy;
    }

    // Getters & Setters
    public List<Player> getPlayers() {
        return players;
    }

    public Map<Integer, List<Card>> getHands() {
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

    public int getNumRemainingHintTokens() {
        return numRemainingHintTokens;
    }

    public void setNumRemainingHintTokens(int numRemainingHintTokens) {
        this.numRemainingHintTokens = Math.min(numRemainingHintTokens, GameRules.MAX_HINT_TOKENS);
        logger.info("Hint tokens updated to " + this.numRemainingHintTokens + " out of " + GameRules.MAX_HINT_TOKENS);
    }

    public int getStrikes() {
        return strikes;
    }

    public void setStrikes(int strikes) {
        this.strikes = strikes;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
        if (gameOver) {
            logger.info("Game is now marked as over.");
        }
    }
    
    public int getFinalTurnsRemaining() {
        return finalTurnsRemaining;
    }
    
    public void setFinalTurnsRemaining(int turns) {
        this.finalTurnsRemaining = turns;
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
        colorHintsToBeRemoved.forEach((cardId) -> {
            cardsShowingColorHintsAndRemainingTurns.remove(cardId);
        });
        
        List<Integer> valueHintsToBeRemoved = new ArrayList<Integer>();
        cardsShowingValueHintsAndRemainingTurns.forEach((cardId, valueHintAndRemainingTurns) -> {
            if (valueHintAndRemainingTurns.getNumTurns()==0) {
                valueHintsToBeRemoved.add(cardId);
            }
        });
        valueHintsToBeRemoved.forEach((cardId) -> {
            cardsShowingValueHintsAndRemainingTurns.remove(cardId);
        });

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
}