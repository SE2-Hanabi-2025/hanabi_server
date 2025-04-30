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
import java.util.stream.Collectors;

public class GameManager {
    private final List<Player> players;
    private final Map<String, List<Card>> hands = new HashMap<>();
    private final Deck deck;
    private final Map<Card.Color, Integer> playedCards = new HashMap<>();
    private final List<Card> discardPile = new ArrayList<>();
    private int hints = GameRules.MAX_HINTS;
    private int strikes = 0;
    private int currentPlayerIndex = 0;
    private boolean gameOver = false;
    private final GameLogger logger = new GameLogger();
    private int finalTurnsRemaining = -1;

    /**
     * Factory method to create a new game with player names
     * @param playerNames List of player names
     * @return A new GameManager instance
     */
    public static GameManager createNewGame(List<String> playerNames) {
        if (playerNames == null || playerNames.isEmpty() || 
            !GameRules.isPlayerCountValid(playerNames.size())) {
            throw new IllegalArgumentException("Invalid number of players: must be between " + 
                GameRules.MIN_PLAYERS + " and " + GameRules.MAX_PLAYERS);
        }
        
        List<Player> playerList = playerNames.stream()
            .map(Player::new)
            .collect(Collectors.toList());
            
        return new GameManager(playerList);
    }

    public GameManager(List<Player> players) {
        if (!GameRules.isPlayerCountValid(players.size())) {
            throw new IllegalArgumentException("Invalid number of players");
        }
        this.players = players;
        this.deck = new Deck();
        
        logger.info("Starting new game with " + players.size() + " players");
        logger.info("Players: " + players.stream().map(Player::getName).collect(Collectors.joining(", ")));

        // Initialize played cards
        for (Card.Color color : Card.Color.values()) {
            playedCards.put(color, 0);
        }

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
        
        logger.info("Game setup completed. " + deck.getRemainingCards() + " cards left in deck.");
        logger.info("Player " + getCurrentPlayerName() + " goes first.");
    }

    public ActionResult playCard(String playerName, int cardIndex) {
        if (!isActionValid(playerName)) {
            return ActionResult.invalid("Not your turn or game is over.");
        }
        
        logger.info(playerName + " attempts to play card at index " + cardIndex);
        return new PlayCardAction(this, playerName, cardIndex).execute();
    }

    public ActionResult discardCard(String playerName, int cardIndex) {
        if (!isActionValid(playerName)) {
            return ActionResult.invalid("Not your turn or game is over.");
        }
        
        logger.info(playerName + " attempts to discard card at index " + cardIndex);
        return new DiscardCardAction(this, playerName, cardIndex).execute();
    }

    public ActionResult giveHint(String fromPlayer, String toPlayer, HintType type, Object value) {
        if (!isActionValid(fromPlayer)) {
            return ActionResult.invalid("Not your turn or game is over.");
        }
        
        if (fromPlayer.equals(toPlayer)) {
            return ActionResult.invalid("Cannot give hint to yourself.");
        }
        
        logger.info(fromPlayer + " attempts to give a " + type + " hint to " + toPlayer + " with value: " + value);
        return new HintAction(this, fromPlayer, toPlayer, type, value).execute();
    }
    
    private boolean isActionValid(String playerName) {
        return !gameOver && isCurrentPlayer(playerName);
    }

    public String getCurrentPlayerName() {
        return players.get(currentPlayerIndex).getName();
    }

    // Game state information
    
    /**
     * Get the complete game status for a specific player
     * @param playerName Name of the player requesting status
     * @return GameStatus object with all relevant game information
     */
    public GameStatus getStatusFor(String playerName) {
        return new GameStatus(
                players,
                getVisibleHands(playerName),
                playedCards,
                discardPile,
                hints,
                strikes,
                gameOver,
                getCurrentPlayerName()
        );
    }
    
    /**
     * Get a specific player's hand
     * @param playerName Name of the player
     * @return List of cards in the player's hand, or null if player not found
     */
    public List<Card> getPlayerHand(String playerName) {
        return hands.get(playerName);
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

    // Helper functions
    private void drawToHand(String playerName) {
        if (!deck.isEmpty()) {
            hands.get(playerName).add(deck.drawCard());
        }
    }

    public void advanceTurn() {
        if (gameOver) {
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        logger.info("Turn advances to " + getCurrentPlayerName());

        if (deck.isEmpty()) {
            if (finalTurnsRemaining == -1) {
                finalTurnsRemaining = players.size();
                logger.info("Deck is empty. Final round started! " + finalTurnsRemaining + " turns remaining.");
            } else {
                finalTurnsRemaining--;
                logger.info("Final round: " + finalTurnsRemaining + " turns remaining.");
                if (finalTurnsRemaining == 0) {
                    gameOver = true;
                    logger.info("Final turn reached. Game over!");
                }
            }
        }
        checkEndCondition();
    }

    private boolean isCurrentPlayer(String playerName) {
        return players.get(currentPlayerIndex).getName().equals(playerName);
    }

    private void checkEndCondition() {
        if (strikes >= GameRules.MAX_STRIKES) {
            gameOver = true;
            logger.error("Game over: maximum strikes reached (" + strikes + ")");
            logFinalScore();
            return;
        }

        boolean isPerfect = playedCards.values().stream().allMatch(v -> v == GameRules.MAX_CARD_VALUE);

        if (isPerfect) {
            gameOver = true;
            logger.info("Game completed perfectly!");
            logFinalScore();
            return;
        }
        
        if (finalTurnsRemaining == 0) {
            gameOver = true;
            logger.info("Game over: final turns reached");
            logFinalScore();
        }
    }

    private void logFinalScore() {
        int totalScore = playedCards.values().stream().mapToInt(Integer::intValue).sum();
        logger.info("Final score: " + totalScore + " out of " + GameRules.MAX_SCORE);
    }

    public void drawCardToHand(String playerName) {
        if (!deck.isEmpty()) {
            Card card = deck.drawCard();
            hands.get(playerName).add(card);
            logger.info(playerName + " drew a new card. " + deck.getRemainingCards() + " cards left in deck.");
        } else {
            logger.info(playerName + " could not draw a card - deck is empty.");
        }
    }

    public void incrementStrikes() {
        strikes++;
        logger.warn("Strike count increased to " + strikes + " out of " + GameRules.MAX_STRIKES);
    }

    /**
     * Get game history logs
     * @return List of game log entries
     */
    public List<String> getGameHistory() {
        return logger.getHistory();
    }

    public GameLogger getLogger() {
        return logger;
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
        if (gameOver) {
            logger.info("Game is now marked as over.");
        }
    }
    
    /**
     * Get the current score
     * @return current score as sum of all played cards' values
     */
    public int getCurrentScore() {
        return playedCards.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    /**
     * Get number of turns remaining in final round
     * @return number of turns remaining or -1 if not in final round
     */
    public int getFinalTurnsRemaining() {
        return finalTurnsRemaining;
    }
}

