package se2.server.hanabi.game;

import lombok.Getter;
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
    private static final String PLAYER_PREFIX = "Player ";
    private static final String ERROR_NOT_YOUR_TURN = "Not your turn or game is over.";
    private static final String CHEAT_PREFIX = "[CHEAT] ";
    @Getter
    private final GameState gameState;
    @Getter
    private final GameLogger logger = new GameLogger();
    private final DrawService drawService = new DrawService();

    /**
     * Factory method to create a new game with players
     * @param isCasualMode sets the game mode
     * @param players List of players
     * @return A new GameManager instance
     */
    public static GameManager createNewGame(List<Player> players, Boolean isCasualMode) {
        if (players == null || players.isEmpty() || 
            !GameRules.isPlayerCountValid(players.size())) {
            throw new IllegalArgumentException("Invalid number of players: must be between " + 
                GameRules.MIN_PLAYERS + " and " + GameRules.MAX_PLAYERS);
        }

        return new GameManager(players, isCasualMode);
    }

    /**
     * Factory method to create a new game with players
     * @param players List of players
     * @return A new GameManager instance
     */
    public static GameManager createNewGame(List<Player> players) {
        return createNewGame(players, false);
    }

    private GameManager(List<Player> players, Boolean isCasualMode) {

        int numTurnsHintsLast = (isCasualMode)? GameRules.TURNS_HINTS_LAST_CASUAL : GameRules.TURNS_HINTS_LAST_DEFAULT;
        this.gameState = new GameState(players, numTurnsHintsLast, logger);

        logger.info("Starting new game with " + players.size() + " players");
        logger.info("Players: " + players.stream().map(p -> p.getId() + " (" + p.getName() + ")").collect(Collectors.joining(", ")));

        gameState.dealInitialCards();

        logger.info("Game setup completed. " + gameState.getDeck().getNumRemainingCards() + " cards left in deck.");
        logger.info(PLAYER_PREFIX + gameState.getCurrentPlayerId() + " goes first.");
    }

    public ActionResult playCard(int playerId, int cardIndex) {
        if (!GameValidator.isPlayerTurn(this, playerId)) {
            return ActionResult.invalid(ERROR_NOT_YOUR_TURN);
        }
        if (!GameValidator.isValidCardIndex(this, playerId, cardIndex)) {
            return ActionResult.invalid("Invalid card index: " + cardIndex);
        }
        logger.info(PLAYER_PREFIX + playerId + " attempts to play card at index " + cardIndex);
        return new PlayCardAction(this, playerId, cardIndex).execute();
    }

    public ActionResult discardCard(int playerId, int cardIndex) {
        if (!GameValidator.isPlayerTurn(this, playerId)) {
            return ActionResult.invalid(ERROR_NOT_YOUR_TURN);
        }
        if (!GameValidator.isValidCardIndex(this, playerId, cardIndex)) {
            return ActionResult.invalid("Invalid card index: " + cardIndex);
        }
        if (!GameValidator.canDiscard(this)) {
            logger.warn(PLAYER_PREFIX + playerId + " attempted to discard but hints are already at maximum.");
            return ActionResult.invalid("Cannot discard: hint tokens are already at maximum (" + GameRules.MAX_HINT_TOKENS + ").");
        }
        logger.info(PLAYER_PREFIX + playerId + " attempts to discard card at index " + cardIndex);
        return new DiscardCardAction(this, playerId, cardIndex).execute();
    }

    public ActionResult giveHint(int fromPlayerId, int toPlayerId, HintType type, Object value) {
        if (!GameValidator.isPlayerTurn(this, fromPlayerId)) {
            return ActionResult.invalid(ERROR_NOT_YOUR_TURN);
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

    public ActionResult defuseStrike(int playerId) {
        if (!gameState.isCurrentPlayer(playerId)) {
            return ActionResult.invalid("You can only defuse on your turn.");
        }
        int strikes = getStrikes();
        if (strikes > 0) {
            setStrikes(strikes - 1);
            logger.info(CHEAT_PREFIX + PLAYER_PREFIX + playerId + " defused a strike! (strikes now: " + (strikes - 1) + ")");
            advanceTurn();
            return ActionResult.success("Strike defused!");
        } else {
            logger.info(CHEAT_PREFIX + PLAYER_PREFIX + playerId + " tried to defuse a strike, but none left.");
            return ActionResult.invalid("No strikes to defuse.");
        }
    }

    public ActionResult addStrikeCheat(int playerId) {
        int strikes = getStrikes();
        if (strikes > 0) {
            setStrikes(strikes + 1);
            logger.info(CHEAT_PREFIX + PLAYER_PREFIX + playerId + " triggered a failed defuse! (strikes now: " + (strikes + 1) + ")");
            advanceTurn();
            return ActionResult.success("Defuse failed, strike added!");
        } else {
            logger.info(CHEAT_PREFIX + PLAYER_PREFIX + playerId + " tried to add a strike, but no strikes present.");
            return ActionResult.invalid("No strikes present, cannot add another.");
        }
    }

    public ActionResult handleDefuseAttempt(Integer playerId, java.util.List<String> sequence, String proximity) {
        // Correct sequence: DOWN, DOWN, UP, DOWN
        java.util.List<String> correctSequence = java.util.Arrays.asList("DOWN", "DOWN", "UP", "DOWN");
        String requiredProximity = "DARK";

        if (sequence == null || proximity == null) {
            return ActionResult.invalid("Missing sequence or proximity for defuse attempt.");
        }
        if (sequence.equals(correctSequence) && requiredProximity.equalsIgnoreCase(proximity)) {
            return defuseStrike(playerId);
        } else {
            return addStrikeCheat(playerId);
        }
    }

    public int getCurrentPlayerId() {
        return gameState.getCurrentPlayerId();
    }

    /**
     * Get the complete game status for a specific player
     * @param playerId ID of the player requesting status
     * @return GameStatus object with all relevant game information
     */
    public GameStatus getStatusFor(int playerId) {
        return new GameStatus(
            gameState.getPlayers(),
            gameState.getPlayerCardIds(playerId),
            gameState.getVisibleHands(playerId),
            gameState.getPlayedCards(),
            gameState.getDiscardPile(),
            gameState.getDeck().getNumRemainingCards(),
            gameState.getCardsShowingColorHints(),
            gameState.getCardsShowingValueHints(),
            gameState.getNumRemainingHintTokens(),
            gameState.getStrikes(),
            gameState.isGameOver(),
            gameState.isGameLost(),
            gameState.getCurrentScore(),
            gameState.getCurrentPlayerId(),
            gameState.getHands().get(playerId)
        );
    }

    public List<Card> getPlayerHand(int playerId) {
        return gameState.getHands().get(playerId);
    }

    public Map<Integer, List<Card>> getVisibleHands(int viewerId) {
        return gameState.getVisibleHands(viewerId);
    }


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

    public void drawCardToHand(int playerId) {
        drawService.drawCardToPlayerHand(this, playerId);
    }

    public synchronized ActionResult incrementStrikes() {
        int currentTurn = gameState.getTurnCounter();
        if (gameState.getLastStrikeTurn() == currentTurn) {
            return ActionResult.success("Strike already given for this round.");
        }
        logger.info("Before increment: Strikes = " + gameState.getStrikes());
        gameState.incrementStrikes();
        gameState.setLastStrikeTurn(currentTurn);
        logger.info("After increment: Strikes = " + gameState.getStrikes());
        gameState.checkEndCondition(); // Ensure game over is set if max strikes reached
        return ActionResult.success("Strike added.");
    }

    public List<String> getGameHistory() {
        return logger.getHistory();
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
        return gameState.getNumRemainingHintTokens();
    }

    public void setNumRemainingHintTokens(int hints) {
        gameState.setNumRemainingHintTokens(hints);
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
    
    public int getCurrentScore() {
        return gameState.getCurrentScore();
    }
    
    public int getFinalTurnsRemaining() {
        return gameState.getFinalTurnsRemaining();
    }
    
    public void setFinalTurnsRemaining(int turns) {
        gameState.setFinalTurnsRemaining(turns);
    }

    public int getNumTurnsHintsLast() {
        return gameState.getNumTurnsHintsLast();
    }

    public void removeCardFromShownHints(int cardId) {
        gameState.removeCardFromShownHints(cardId);
    }

}

