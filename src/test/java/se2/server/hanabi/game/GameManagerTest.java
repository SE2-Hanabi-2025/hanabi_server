package se2.server.hanabi.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se2.server.hanabi.api.GameStatus;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.util.GameRules;


import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    private GameManager game;
    private Player player1 = new Player("alice");
    private Player player2 = new Player("bob");
    private Player player3 = new Player("charlie");
    private Player player4 = new Player("david");

    @BeforeEach
    void setUp() {
        game = GameManager.createNewGame(List.of(player1, player2,player3));
    }

    @Test
    void testCreateNewGame() {
        // Test factory method
        assertNotNull(game);
        assertEquals(3, game.getPlayers().size());
        assertEquals(player1.getId(), game.getPlayers().get(0).getId());
        assertEquals(3, game.getHands().size());
    }

    @Test
    void testInitialGameState() {
        // Test initial state values
        assertFalse(game.isGameOver());
        assertEquals(GameRules.MAX_HINT_TOKENS, game.getHints());
        assertEquals(0, game.getStrikes());
        assertEquals(0, game.getCurrentPlayerIndex());
        assertEquals(player1.getId(), game.getCurrentPlayerId());
        
        // Test initial hands
        Map<Integer, List<Card>> hands = game.getHands();
        assertEquals(GameRules.HAND_SIZE_SMALL_GROUP, hands.get(player1.getId()).size());
        
        // Test played cards (all should be 0)
        Map<Card.Color, Integer> playedCards = game.getPlayedCards();
        for (Card.Color color : Card.Color.values()) {
            assertEquals(0, playedCards.get(color));
        }
    }

    @Test
    void testPlayCardWrongTurn() {
        // Player 2 tries to play when it's Player 1's turn
        ActionResult result = game.playCard(2, 0);
        assertFalse(result.isSuccess());
        assertEquals("Not your turn or game is over.", result.getMessage());
    }
    
    @Test
    void testGiveHintToSelf() {
        // Player 1 tries to give hint to self
        ActionResult result = game.giveHint(player1.getId(), player1.getId(), HintType.COLOR, Card.Color.RED);
        assertFalse(result.isSuccess());
        assertEquals("Cannot give hint to yourself.", result.getMessage());
    }

    @Test
    void testNoActionAfterGameOver() {
        // Force game over
        game.setGameOver(true);
        
        // Try to perform actions
        ActionResult playResult = game.playCard(1, 0);
        assertFalse(playResult.isSuccess());
        
        ActionResult discardResult = game.discardCard(1, 0);
        assertFalse(discardResult.isSuccess());
        
        ActionResult hintResult = game.giveHint(1, 2, HintType.VALUE, 1);
        assertFalse(hintResult.isSuccess());
    }

    @Test
    void testDiscardCardAfterGameOver() {
    
        game.setGameOver(true);
        ActionResult result = game.discardCard(1, 0);

        assertFalse(result.isSuccess());
        assertEquals("Not your turn or game is over.", result.getMessage());
    }

    @Test
    void testGameStatusForPlayer() {
        GameStatus status = game.getStatusFor(player1.getId());
        
        assertNotNull(status);
        assertEquals(3, status.getPlayers().size());
        assertEquals(player1.getId(), status.getCurrentPlayerId());
        
        // Player 1 shouldn't see their own hand in visible hands
        Map<Integer, List<Card>> visibleHands = status.getVisibleHands();
        assertFalse(visibleHands.containsKey(player1.getId()));
        assertTrue(visibleHands.containsKey(player2.getId()));
        assertTrue(visibleHands.containsKey(player3.getId()));
    }

    @Test
    void testGetPlayerHand() {
        List<Card> hand = game.getPlayerHand(player1.getId());
        assertNotNull(hand);
        assertEquals(GameRules.HAND_SIZE_SMALL_GROUP, hand.size());
    }

    @Test
    void testSetHintsWithinMaximum() {
        game.setNumRemainingHintTokens(GameRules.MAX_HINT_TOKENS + 2); // Try to set hints beyond max
        assertEquals(GameRules.MAX_HINT_TOKENS, game.getHints()); // Should be capped at max
    }

    @Test
    void testGetCurrentScore() {
        // Initial score should be 0
        assertEquals(0, game.getCurrentScore());
        
        // Add some played cards
        game.getPlayedCards().put(Card.Color.RED, 3);
        game.getPlayedCards().put(Card.Color.BLUE, 2);
        
        // Score should be 3 + 2 = 5
        assertEquals(5, game.getCurrentScore());
    }

    @Test
    void testGameHistory() {
        List<String> history = game.getGameHistory();
        assertNotNull(history);
        assertFalse(history.isEmpty());
        
        // Initial history should contain setup messages
        boolean foundSetupMessage = false;
        for (String entry : history) {
            if (entry.contains("Starting new game with")) {
                foundSetupMessage = true;
                break;
            }
        }
        assertTrue(foundSetupMessage);
    }

    @Test
    void testPlayCardWithInvalidIndex() {

        ActionResult result = game.playCard(player1.getId(), -1); 

        assertFalse(result.isSuccess());
        assertEquals("Invalid card index: -1", result.getMessage());

        result = game.playCard(player1.getId(), 10); 

       
        assertFalse(result.isSuccess());
        assertEquals("Invalid card index: 10", result.getMessage());
    }

    @Test
    void testGiveColorHint() {
        int hintFrom = game.getCurrentPlayerId();
        int hintTo = (hintFrom+1) % game.getPlayers().size();
        Card card; 
        if (game.getHands() != null 
        && game.getHands().get(hintTo) != null 
        && game.getHands().get(hintTo).get(0) != null) {
            card = game.getHands().get(hintTo).get(0);
        
        ActionResult result = game.giveHint(hintFrom, hintTo, HintType.COLOR, card.getColor());
        assertTrue(result.isSuccess());
        assertEquals("Hint given", result.getMessage());
        }
    }

    @Test
    void testGiveValueHint() {
        int hintFrom = game.getCurrentPlayerId();
        int hintTo = (hintFrom+1) % game.getPlayers().size();
        Card card; 
        if (game.getHands() != null 
        && game.getHands().get(hintTo) != null 
        && game.getHands().get(hintTo).get(0) != null) {
            card = game.getHands().get(hintTo).get(0);
        
        ActionResult result = game.giveHint(hintFrom, hintTo, HintType.VALUE, card.getValue());
        assertTrue(result.isSuccess());
        assertEquals("Hint given", result.getMessage());
        }
    }

    @Test
    void testGiveHintToInvalidPlayer() {
  
        ActionResult result = game.giveHint(player1.getId(), player4.getId(), HintType.COLOR, Card.Color.RED);

        assertFalse(result.isSuccess());
        assertEquals("Target player does not exist in this game.", result.getMessage());
    }

    @Test
    void testGiveInvalidHintType() {

        ActionResult result = game.giveHint(player1.getId(), player2.getId(), HintType.COLOR, "notvalid");

        assertFalse(result.isSuccess());
        assertEquals("Invalid hint type or value.", result.getMessage());
    }        


    @Test
    void testGiveHintWithoutHintTokensAvailable() {
        
        game.setNumRemainingHintTokens(0);
        
        ActionResult result = game.giveHint(player1.getId(), player2.getId(), HintType.COLOR, Card.Color.RED);

        assertFalse(result.isSuccess());
        assertEquals("No hint tokens available.", result.getMessage());
    }


    @Test
    void testHintTokenExhaustion() {
        // Exhaust all hint tokens
        game.setNumRemainingHintTokens(0);

        // Attempt to give a hint
        ActionResult result = game.giveHint(player1.getId(), player2.getId(), HintType.COLOR, Card.Color.RED);

        // Verify the action fails with the correct message
        assertFalse(result.isSuccess(), "Giving a hint should fail when no hint tokens are available.");
        assertEquals("No hint tokens available.", result.getMessage(), "Expected message for hint token exhaustion.");
    }


    @Test
    void testTurnAdvancement() {
        // Verify initial turn
        assertEquals(player1.getId(), game.getCurrentPlayerId(), "Initial turn should belong to Player 1.");

        // Advance turn
        game.advanceTurn();
        assertEquals(player2.getId(), game.getCurrentPlayerId(), "Turn should advance to Player 2.");

        // Advance turn again
        game.advanceTurn();
        assertEquals(player3.getId(), game.getCurrentPlayerId(), "Turn should advance to Player 3.");

        // Wrap around to Player 1
        game.advanceTurn();
        assertEquals(player1.getId(), game.getCurrentPlayerId(), "Turn should wrap around to Player 1.");
    }

    @Test
    void testAdvanceTurnEndCheck() {
        game.setStrikes(GameRules.MAX_STRIKES);
        game.advanceTurn();
        assertTrue(game.isGameOver());
    }

    @Test
    void testFailedAdvanceTurnWhenGameOVer() {
        game.setGameOver(true);
        int currentPlayerId = game.getCurrentPlayerId();
        game.advanceTurn();
        assertEquals(currentPlayerId, game.getCurrentPlayerId());
    }

    @Test
    void testGameInitializationEdgeCases() {
        // Test with minimum players
        GameManager minGame = GameManager.createNewGame(Arrays.asList(player1,player2));
        assertNotNull(minGame, "Game should initialize with minimum players.");
        assertEquals(2, minGame.getPlayers().size(), "Game should have 2 players.");

        // Test with maximum players
        GameManager maxGame = GameManager.createNewGame(Arrays.asList(player1,player1,player2,player2,player3));
        assertNotNull(maxGame, "Game should initialize with maximum players.");
        assertEquals(5, maxGame.getPlayers().size(), "Game should have 5 players.");

        // Test with invalid player counts
        Exception tooFewPlayersException = assertThrows(IllegalArgumentException.class, () -> {
            GameManager.createNewGame(Arrays.asList(player1));
        });
        assertTrue(tooFewPlayersException.getMessage().contains("Invalid number of players"), "Expected exception for too few players.");

        Exception tooManyPlayersException = assertThrows(IllegalArgumentException.class, () -> {
            GameManager.createNewGame(Arrays.asList(player1, player2, player1, player2,player1, player2));
        });
        assertTrue(tooManyPlayersException.getMessage().contains("Invalid number of players"), "Expected exception for too many players.");
    }


    @Test
    void testInvalidCardPlay() {
        // Simulate a scenario where the card at index 0 is invalid
        game.getHands().get(player1.getId()).set(0, new Card(5, Card.Color.RED)); // Set an invalid card

        // Attempt to play the invalid card
        ActionResult result = game.playCard(player1.getId(), 0);

        // Verify the action fails with the correct message
        assertFalse(result.isSuccess(), "Playing an invalid card should fail.");
        assertEquals("Wrong card!", result.getMessage(), "Expected message for invalid card play.");
    }


    @Test
    public void testSetStrikes() {
        game.setStrikes(2);
        assertEquals(2, game.getStrikes(), "Strikes should be updated correctly.");
    }

    @Test
    public void testGetGameState() {
        GameState state = game.getGameState();
        assertNotNull(state, "GameState should not be null.");
        assertEquals(game.getStrikes(), state.getStrikes(), "GameState should reflect the correct strikes.");
    }

    @Test
    public void testLogFinalScore() {
        game.getPlayedCards().put(Card.Color.RED, 5);
        game.getPlayedCards().put(Card.Color.BLUE, 4);
        game.logFinalScore();
        // Verify the log contains the correct final score (mock logger or capture output if necessary)
    }

    @Test 
    public void testDiscardCard() {        
        game.setNumRemainingHintTokens(0);
        ActionResult result = game.discardCard(player1.getId(), 0);
        assertTrue(result.isSuccess());
    }

    @Test
    public void testDiscardCardWithInvalidIndex() {
        ActionResult result = game.discardCard(player1.getId(), -1); // Invalid index
        assertFalse(result.isSuccess(), "Discarding with an invalid index should fail.");
        assertEquals("Invalid card index: -1", result.getMessage(), "Expected message for invalid card index.");
    }

    @Test
    public void testCannotDiscardWhenHintsAtMaximum() {
        game.setNumRemainingHintTokens(GameRules.MAX_HINT_TOKENS);
        ActionResult result = game.discardCard(player1.getId(), 0);
        assertFalse(result.isSuccess(), "Discarding when hints are at maximum should fail.");
        assertEquals("Cannot discard: hint tokens are already at maximum (8).", result.getMessage(), "Expected message for maximum hints.");
    }

    @Test
    public void testGetVisibleHands() {
        Map<Integer, List<Card>> visibleHands = game.getVisibleHands(player1.getId());
        Map<Integer, List<Card>> expected = new HashMap<>(game.getHands());
        expected.remove(player1.getId());
        assertEquals(visibleHands, expected );
    }

    @Test
    void testHandleDefuseAttempt_CorrectSequenceAndProximity() {
        int playerId = player1.getId();
        game.setStrikes(2); // Ensure there is at least one strike to defuse
        java.util.List<String> correctSequence = java.util.Arrays.asList("DOWN", "DOWN", "UP", "DOWN");
        String proximity = "DARK";
        ActionResult result = game.handleDefuseAttempt(playerId, correctSequence, proximity);
        assertTrue(result.isSuccess());
        assertEquals("Strike defused!", result.getMessage());
        assertEquals(1, game.getStrikes()); // Should have one less strike
    }

    @Test
    void testHandleDefuseAttempt_WrongSequence() {
        int playerId = player1.getId();
        game.setStrikes(2);
        java.util.List<String> wrongSequence = java.util.Arrays.asList("DOWN", "UP", "DOWN", "DOWN");
        String proximity = "DARK";
        ActionResult result = game.handleDefuseAttempt(playerId, wrongSequence, proximity);
        assertTrue(result.isSuccess()); // addStrikeCheat returns success
        assertEquals("Defuse failed, strike added!", result.getMessage());
        assertEquals(3, game.getStrikes()); // Should have one more strike
    }

    @Test
    void testHandleDefuseAttempt_WrongProximity() {
        int playerId = player1.getId();
        game.setStrikes(2);
        java.util.List<String> correctSequence = java.util.Arrays.asList("DOWN", "DOWN", "UP", "DOWN");
        String proximity = "LIGHT";
        ActionResult result = game.handleDefuseAttempt(playerId, correctSequence, proximity);
        assertTrue(result.isSuccess()); // addStrikeCheat returns success
        assertEquals("Defuse failed, strike added!", result.getMessage());
        assertEquals(3, game.getStrikes());
    }

    @Test
    void testHandleDefuseAttempt_MissingParams() {
        int playerId = player1.getId();
        ActionResult result = game.handleDefuseAttempt(playerId, null, null);
        assertFalse(result.isSuccess());
        assertEquals("Missing sequence or proximity for defuse attempt.", result.getMessage());
    }

    @Test
    void testHandleDefuseAttempt_NoStrikesToDefuse() {
        int playerId = player1.getId();
        game.setStrikes(0);
        java.util.List<String> correctSequence = java.util.Arrays.asList("DOWN", "DOWN", "UP", "DOWN");
        String proximity = "DARK";
        ActionResult result = game.handleDefuseAttempt(playerId, correctSequence, proximity);
        assertFalse(result.isSuccess());
        assertEquals("No strikes to defuse.", result.getMessage());

    }
}