package se2.server.hanabi.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.api.GameStatus;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.GameRules;


import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    private GameManager gameManager;
    private List<Integer> playerIds = Arrays.asList(1, 2, 3);

    @BeforeEach
    void setUp() {
        gameManager = GameManager.createNewGame(playerIds);
    }

    @Test
    void testCreateNewGame() {
        // Test factory method
        GameManager game = GameManager.createNewGame(playerIds);
        assertNotNull(game);
        assertEquals(3, game.getPlayers().size());
        assertEquals(1, game.getPlayers().get(0).getId());
        assertEquals(3, game.getHands().size());
    }

    @Test
    void testInvalidPlayerCount() {
        // Test with too few players
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            GameManager.createNewGame(Arrays.asList(1));
        });
        assertTrue(exception.getMessage().contains("Invalid number of players"));
        
        // Test with too many players
        exception = assertThrows(IllegalArgumentException.class, () -> {
            GameManager.createNewGame(Arrays.asList(1, 2, 3, 4, 5, 6));
        });
        assertTrue(exception.getMessage().contains("Invalid number of players"));
    }

    @Test
    void testInitialGameState() {
        // Test initial state values
        assertFalse(gameManager.isGameOver());
        assertEquals(GameRules.MAX_HINTS, gameManager.getHints());
        assertEquals(0, gameManager.getStrikes());
        assertEquals(0, gameManager.getCurrentPlayerIndex());
        assertEquals(1, gameManager.getCurrentPlayerId());
        
        // Test initial hands
        Map<Integer, List<Card>> hands = gameManager.getHands();
        assertEquals(GameRules.HAND_SIZE_SMALL_GROUP, hands.get(1).size());
        
        // Test played cards (all should be 0)
        Map<Card.Color, Integer> playedCards = gameManager.getPlayedCards();
        for (Card.Color color : Card.Color.values()) {
            assertEquals(0, playedCards.get(color));
        }
    }

    @Test
    void testTurnManagement() {
        assertEquals(1, gameManager.getCurrentPlayerId());
        gameManager.advanceTurn();
        assertEquals(2, gameManager.getCurrentPlayerId());
        gameManager.advanceTurn();
        assertEquals(3, gameManager.getCurrentPlayerId());
        gameManager.advanceTurn();
        assertEquals(1, gameManager.getCurrentPlayerId());
    }

    @Test
    void testPlayCardWrongTurn() {
        // Player 2 tries to play when it's Player 1's turn
        ActionResult result = gameManager.playCard(2, 0);
        assertFalse(result.isSuccess());
        assertEquals("Not your turn or game is over.", result.getMessage());
    }
    
    @Test
    void testGiveHintToSelf() {
        // Player 1 tries to give hint to self
        ActionResult result = gameManager.giveHint(1, 1, HintType.COLOR, Card.Color.RED);
        assertFalse(result.isSuccess());
        assertEquals("Cannot give hint to yourself.", result.getMessage());
    }

    @Test
    void testNoActionAfterGameOver() {
        // Force game over
        gameManager.setGameOver(true);
        
        // Try to perform actions
        ActionResult playResult = gameManager.playCard(1, 0);
        assertFalse(playResult.isSuccess());
        
        ActionResult discardResult = gameManager.discardCard(1, 0);
        assertFalse(discardResult.isSuccess());
        
        ActionResult hintResult = gameManager.giveHint(1, 2, HintType.VALUE, 1);
        assertFalse(hintResult.isSuccess());
    }

    @Test
    void testDiscardCardAfterGameOver() {
    
        gameManager.setGameOver(true);
        ActionResult result = gameManager.discardCard(1, 0);

        assertFalse(result.isSuccess());
        assertEquals("Not your turn or game is over.", result.getMessage());
    }

    @Test
    void testGameStatusForPlayer() {
        GameStatus status = gameManager.getStatusFor(1);
        
        assertNotNull(status);
        assertEquals(3, status.getPlayers().size());
        assertEquals(1, status.getCurrentPlayerId());
        
        // Player 1 shouldn't see their own hand in visible hands
        Map<Integer, List<Card>> visibleHands = status.getVisibleHands();
        assertFalse(visibleHands.containsKey(1));
        assertTrue(visibleHands.containsKey(2));
        assertTrue(visibleHands.containsKey(3));
    }

    @Test
    void testGetPlayerHand() {
        List<Card> hand = gameManager.getPlayerHand(1);
        assertNotNull(hand);
        assertEquals(GameRules.HAND_SIZE_SMALL_GROUP, hand.size());
    }

    @Test
    void testSetHintsWithinMaximum() {
        gameManager.setHints(GameRules.MAX_HINTS + 2); // Try to set hints beyond max
        assertEquals(GameRules.MAX_HINTS, gameManager.getHints()); // Should be capped at max
    }

    @Test
    void testGetCurrentScore() {
        // Initial score should be 0
        assertEquals(0, gameManager.getCurrentScore());
        
        // Add some played cards
        gameManager.getPlayedCards().put(Card.Color.RED, 3);
        gameManager.getPlayedCards().put(Card.Color.BLUE, 2);
        
        // Score should be 3 + 2 = 5
        assertEquals(5, gameManager.getCurrentScore());
    }

    @Test
    void testGameHistory() {
        List<String> history = gameManager.getGameHistory();
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

        ActionResult result = gameManager.playCard(1, -1); 

        assertFalse(result.isSuccess());
        assertEquals("Invalid card index: -1", result.getMessage());

        result = gameManager.playCard(1, 10); 

       
        assertFalse(result.isSuccess());
        assertEquals("Invalid card index: 10", result.getMessage());
    }

    @Test
    void testGiveHintWithoutHintsAvailable() {
        
        gameManager.setHints(0);
        
        ActionResult result = gameManager.giveHint(1, 2, HintType.COLOR, Card.Color.RED);

        assertFalse(result.isSuccess());
        assertEquals("No hint tokens available.", result.getMessage());
    }


    @Test
    void testEmptyDeckScenario() {
        // Simulate an empty deck
        while (!gameManager.getDeck().isEmpty()) {
            gameManager.getDeck().drawCard();
        }

        // Verify the deck is empty
        assertTrue(gameManager.getDeck().isEmpty(), "Deck should be empty.");

        // Attempt to play a card when the deck is empty
        ActionResult result = gameManager.playCard(1, 0);

        // Verify the action fails with the correct message
        assertFalse(result.isSuccess(), "Playing a card should fail when the deck is empty.");
        assertEquals("No cards left in the deck.", result.getMessage(), "Expected message for empty deck scenario.");
    }

 

    @Test
    void testHintTokenExhaustion() {
        // Exhaust all hint tokens
        gameManager.setHints(0);

        // Attempt to give a hint
        ActionResult result = gameManager.giveHint(1, 2, HintType.COLOR, Card.Color.RED);

        // Verify the action fails with the correct message
        assertFalse(result.isSuccess(), "Giving a hint should fail when no hint tokens are available.");
        assertEquals("No hint tokens available.", result.getMessage(), "Expected message for hint token exhaustion.");
    }


    @Test
    void testTurnAdvancement() {
        // Verify initial turn
        assertEquals(1, gameManager.getCurrentPlayerId(), "Initial turn should belong to Player 1.");

        // Advance turn
        gameManager.advanceTurn();
        assertEquals(2, gameManager.getCurrentPlayerId(), "Turn should advance to Player 2.");

        // Advance turn again
        gameManager.advanceTurn();
        assertEquals(3, gameManager.getCurrentPlayerId(), "Turn should advance to Player 3.");

        // Wrap around to Player 1
        gameManager.advanceTurn();
        assertEquals(1, gameManager.getCurrentPlayerId(), "Turn should wrap around to Player 1.");
    }

    @Test
    void testGameInitializationEdgeCases() {
        // Test with minimum players
        GameManager minGame = GameManager.createNewGame(Arrays.asList(1, 2));
        assertNotNull(minGame, "Game should initialize with minimum players.");
        assertEquals(2, minGame.getPlayers().size(), "Game should have 2 players.");

        // Test with maximum players
        GameManager maxGame = GameManager.createNewGame(Arrays.asList(1, 2, 3, 4, 5));
        assertNotNull(maxGame, "Game should initialize with maximum players.");
        assertEquals(5, maxGame.getPlayers().size(), "Game should have 5 players.");

        // Test with invalid player counts
        Exception tooFewPlayersException = assertThrows(IllegalArgumentException.class, () -> {
            GameManager.createNewGame(Arrays.asList(1));
        });
        assertTrue(tooFewPlayersException.getMessage().contains("Invalid number of players"), "Expected exception for too few players.");

        Exception tooManyPlayersException = assertThrows(IllegalArgumentException.class, () -> {
            GameManager.createNewGame(Arrays.asList(1, 2, 3, 4, 5, 6));
        });
        assertTrue(tooManyPlayersException.getMessage().contains("Invalid number of players"), "Expected exception for too many players.");
    }

    @Test
    void testGameOverByMaximumStrikes() {
        // Simulate reaching maximum strikes
        for (int i = 0; i < GameRules.MAX_STRIKES; i++) {
            gameManager.incrementStrikes();
        }

        // Verify the game is over
        assertTrue(gameManager.isGameOver(), "Game should be over after reaching maximum strikes.");
    }

    @Test
    void testInvalidCardPlay() {
        // Simulate a scenario where the card at index 0 is invalid
        gameManager.getHands().get(1).set(0, new Card(5, Card.Color.RED)); // Set an invalid card

        // Attempt to play the invalid card
        ActionResult result = gameManager.playCard(1, 0);

        // Verify the action fails with the correct message
        assertFalse(result.isSuccess(), "Playing an invalid card should fail.");
        assertEquals("Wrong card!", result.getMessage(), "Expected message for invalid card play.");
    }

}