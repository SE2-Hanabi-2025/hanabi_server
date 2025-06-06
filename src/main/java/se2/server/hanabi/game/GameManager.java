package se2.server.hanabi.game;

import se2.server.hanabi.api.GameStatus;
import se2.server.hanabi.game.actions.DiscardCardAction;
import se2.server.hanabi.game.actions.HintAction;
import se2.server.hanabi.game.actions.PlayCardAction;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.util.GameRules;
import se2.server.hanabi.services.DrawService;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameManager {
    private final GameState gameState;
    private final GameLogger logger = new GameLogger();
    private final DrawService drawService = new DrawService();

    /**
     * Factory method to create a new game with player IDs
     * @param playerIds List of player IDs
     * @return A new GameManager instance
     */
    public static GameManager createNewGame(List<Integer> playerIds) {
        if (playerIds == null || playerIds.isEmpty() || 
            !GameRules.isPlayerCountValid(playerIds.size())) {
            throw new IllegalArgumentException("Invalid number of players: must be between " + 
                GameRules.MIN_PLAYERS + " and " + GameRules.MAX_PLAYERS);
        }

        // Create Player objects from playerIds
        List<Player> players = playerIds.stream()
                                        .map(id -> new Player(id))
                                        .collect(Collectors.toList());

        return new GameManager(players);
    }

    public GameManager(List<Player> players) {
        if (!GameRules.isPlayerCountValid(players.size())) {
            throw new IllegalArgumentException("Invalid number of players");
        }

        this.gameState = new GameState(players, logger);

        logger.info("Starting new game with " + players.size() + " players");
        logger.info("Players: " + players.stream().map(p -> p.getId() + " (" + p.getName() + ")").collect(Collectors.joining(", ")));

        gameState.dealInitialCards();

        logger.info("Game setup completed. " + gameState.getDeck().getRemainingCards() + " cards left in deck.");
        logger.info("Player " + gameState.getCurrentPlayerId() + " goes first.");
    }

    public ActionResult playCard(int playerId, int cardIndex) {
        if (!GameValidator.isPlayerTurn(this, playerId)) {
            return ActionResult.invalid("Not your turn or game is over.");
        }
        if (!GameValidator.isValidCardIndex(this, playerId, cardIndex)) {
            return ActionResult.invalid("Invalid card index: " + cardIndex);
        }
        logger.info("Player " + playerId + " attempts to play card at index " + cardIndex);
        return new PlayCardAction(this, playerId, cardIndex).execute();
    }

    public ActionResult discardCard(int playerId, int cardIndex) {
        if (!GameValidator.isPlayerTurn(this, playerId)) {
            return ActionResult.invalid("Not your turn or game is over.");
        }
        if (!GameValidator.isValidCardIndex(this, playerId, cardIndex)) {
            return ActionResult.invalid("Invalid card index: " + cardIndex);
        }
        if (!GameValidator.canDiscard(this)) {
            logger.warn("Player " + playerId + " attempted to discard but hints are already at maximum.");
            return ActionResult.invalid("Cannot discard: hint tokens are already at maximum (" + GameRules.MAX_HINTS + ").");
        }
        logger.info("Player " + playerId + " attempts to discard card at index " + cardIndex);
        return new DiscardCardAction(this, playerId, cardIndex).execute();
    }

    public ActionResult giveHint(int fromPlayerId, int toPlayerId, HintType type, Object value) {
        if (!GameValidator.isPlayerTurn(this, fromPlayerId)) {
            return ActionResult.invalid("Not your turn or game is over.");
        }
        if (!GameValidator.isNotSelfHint(fromPlayerId, toPlayerId)) {
            return ActionResult.invalid("Cannot give hint to yourself.");
        }
        if (!GameValidator.hasEnoughHints(this)) {
            return ActionResult.invalid("No hint tokens available.");
        }
        if (!GameValidator.playerExists(this, toPlayerId)) {
            return ActionResult.invalid("Target player does not exist in this game.");
        }
        if (!GameValidator.isValidHintTypeAndValue(type, value)) {
            return ActionResult.invalid("Invalid hint type or value.");
        }

        ActionResult result = new HintAction(this, fromPlayerId, toPlayerId, type, value).execute();
        if (!result.isSuccess()) {
            return ActionResult.failure("Hint failed: " + result.getMessage());
        }

        return result;
    }

    public int getCurrentPlayerId() {
        return gameState.getCurrentPlayerId();
    }

    // Game state information
    
    /**
     * Get the complete game status for a specific player
     * @param playerId ID of the player requesting status
     * @return GameStatus object with all relevant game information
     */
    public GameStatus getStatusFor(int playerId) {

        int currentPlayerId = gameState.getCurrentPlayerId();

        return new GameStatus(
            gameState.getPlayers(),
            gameState.getVisibleHands(currentPlayerId),
            gameState.getPlayedCards(),
            gameState.getDiscardPile(),
            gameState.getHints(),
            gameState.getStrikes(),
            gameState.isGameOver(),
            String.valueOf(currentPlayerId) // Pass current player ID as a string
        );
    }
    
    /**
     * Get a specific player's hand
     * @param playerId ID of the player
     * @return List of cards in the player's hand, or null if player not found
     */
    public List<Card> getPlayerHand(int playerId) {
        return gameState.getHands().get(playerId);
    }

    /**
     * Get all hands except the specified player's
     * @param viewerId ID of the player who should not see their own hand
     * @return Map of player IDs to their hand of cards
     */
    public Map<Integer, List<Card>> getVisibleHands(int viewerId) {
        return gameState.getVisibleHands(viewerId);
    }

    // Helper functions

    public void advanceTurn() {
        if (gameState.advanceTurn()) {
            drawService.checkDeckEmptyStatus(this);
            if (gameState.checkEndCondition()) {
                logFinalScore();
            }
        }
    }

    public void logFinalScore() {
        int totalScore = gameState.getCurrentScore();
        logger.info("Final score: " + totalScore + " out of " + GameRules.MAX_SCORE);
    }

    /**
     * Draw a card to a player's hand from the deck
     * 
     * @param playerId the ID of the player who should draw a card
     * @return the drawn card or null if no card was drawn
     */
    public Card drawCardToHand(int playerId) {
        return drawService.drawCardToPlayerHand(this, playerId);
    }

    public void incrementStrikes() {
        logger.info("Before increment: Strikes = " + gameState.getStrikes());
        gameState.incrementStrikes();
        logger.info("After increment: Strikes = " + gameState.getStrikes());
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
    
    // GameState delegating methods
    
    public GameState getGameState() {
        return gameState;
    }
    
    public List<Player> getPlayers() {
        return gameState.getPlayers();
    }

    public Map<Integer, List<Card>> getHands() {
        return gameState.getHands();
    }

    public Deck getDeck() {
        return gameState.getDeck();
    }

    public Map<Card.Color, Integer> getPlayedCards() {
        return gameState.getPlayedCards();
    }

    public List<Card> getDiscardPile() {
        return gameState.getDiscardPile();
    }

    public int getHints() {
        return gameState.getHints();
    }

    public void setHints(int hints) {
        gameState.setHints(hints);
    }

    public int getStrikes() {
        return gameState.getStrikes();
    }

    public void setStrikes(int strikes) {
        gameState.setStrikes(strikes);
    }

    public int getCurrentPlayerIndex() {
        return gameState.getPlayers().indexOf(
            gameState.getPlayers().stream()
                .filter(p -> p.getId() == gameState.getCurrentPlayerId())
                .findFirst()
                .orElse(null)
        );
    }

    public boolean isGameOver() {
        return gameState.isGameOver();
    }

    public void setGameOver(boolean gameOver) {
        gameState.setGameOver(gameOver);
    }
    
    /**
     * Get the current score
     * @return current score as sum of all played cards' values
     */
    public int getCurrentScore() {
        return gameState.getCurrentScore();
    }
    
    /**
     * Get number of turns remaining in final round
     * @return number of turns remaining or -1 if not in final round
     */
    public int getFinalTurnsRemaining() {
        return gameState.getFinalTurnsRemaining();
    }
    
    /**
     * Set the number of turns remaining in the final round
     * @param turns number of turns remaining
     */
    public void setFinalTurnsRemaining(int turns) {
        gameState.setFinalTurnsRemaining(turns);
    }

}

