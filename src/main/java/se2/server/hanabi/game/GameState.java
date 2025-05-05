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
    private final Map<String, List<Card>> hands = new HashMap<>();
    private final Deck deck;
    private final Map<Card.Color, Integer> playedCards = new HashMap<>();
    private final List<Card> discardPile = new ArrayList<>();
    private int hints = GameRules.MAX_HINTS;
    private int strikes = 0;
    private int currentPlayerIndex = 0;
    private boolean gameOver = false;
    private int finalTurnsRemaining = -1;
    private final GameLogger logger;

    /**
     * Constructor for the game state
     * @param players the list of players
     * @param logger the game logger for logging game events
     */
    public GameState(List<Player> players, GameLogger logger) {
        this.players = players;
        this.logger = logger;
        this.deck = new Deck();
        
        initializePlayedCards();
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
            hands.put(player.getName(), hand);
        }
    }
    
    /**
     * Checks if a player is the current player
     * @param playerName the name of the player to check
     * @return true if the player is the current player, false otherwise
     */
    public boolean isCurrentPlayer(String playerName) {
        return players.get(currentPlayerIndex).getName().equals(playerName);
    }
    
    /**
     * Checks if an action is valid
     * @param playerName the name of the player attempting the action
     * @return true if the action is valid, false otherwise
     */
    public boolean isActionValid(String playerName) {
        return !gameOver && isCurrentPlayer(playerName);
    }
    
    /**
     * Check if a card index is valid for a player's hand
     */
    public boolean isValidCardIndex(String playerName, int cardIndex) {
        List<Card> hand = hands.get(playerName);
        return hand != null && cardIndex >= 0 && cardIndex < hand.size();
    }
    
    /**
     * Check if a player exists in the game
     */
    public boolean playerExists(String playerName) {
        return players.stream().anyMatch(p -> p.getName().equals(playerName));
    }

    /**
     * Get the current player's name
     * @return the name of the current player
     */
    public String getCurrentPlayerName() {
        return players.get(currentPlayerIndex).getName();
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
        logger.info("Turn advances to " + getCurrentPlayerName());
        
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
        strikes++;
        logger.warn("Strike count increased to " + strikes + " out of " + GameRules.MAX_STRIKES);
    }
    
    /**
     * Get all hands except the specified player's
     * @param viewer Name of the player who should not see their own hand
     * @return Map of player names to their hand of cards
     */
    public Map<String, List<Card>> getVisibleHands(String viewer) {
        Map<String, List<Card>> copy = new HashMap<>();
        for (Map.Entry<String, List<Card>> entry : hands.entrySet()) {
            if (!entry.getKey().equals(viewer)) {
                copy.put(entry.getKey(), new ArrayList<>(entry.getValue()));
            }
        }
        return copy;
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
        this.hints = Math.min(hints, GameRules.MAX_HINTS);
        logger.info("Hint tokens updated to " + this.hints + " out of " + GameRules.MAX_HINTS);
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
}