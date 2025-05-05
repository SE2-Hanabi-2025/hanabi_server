package se2.server.hanabi.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.api.GameStatus;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.game.HintType;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.GameRules;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class GameManagerTest {

    private GameManager gameManager;
    private List<String> players = Arrays.asList("Player1", "Player2", "Player3");

    @BeforeEach
    void setUp() {
        gameManager = GameManager.createNewGame(players);
    }

    @Test
    void testCreateNewGame() {
        // Test factory method
        GameManager game = GameManager.createNewGame(players);
        assertNotNull(game);
        assertEquals(3, game.getPlayers().size());
        assertEquals("Player1", game.getPlayers().get(0).getName());
        assertEquals(3, game.getHands().size());
    }

    @Test
    void testInvalidPlayerCount() {
        // Test with too few players
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            GameManager.createNewGame(Arrays.asList("OnlyOne"));
        });
        assertTrue(exception.getMessage().contains("Invalid number of players"));
        
        // Test with too many players
        exception = assertThrows(IllegalArgumentException.class, () -> {
            GameManager.createNewGame(Arrays.asList("P1", "P2", "P3", "P4", "P5", "P6"));
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
        assertEquals("Player1", gameManager.getCurrentPlayerName());
        
        // Test initial hands
        Map<String, List<Card>> hands = gameManager.getHands();
        assertEquals(GameRules.HAND_SIZE_SMALL_GROUP, hands.get("Player1").size());
        
        // Test played cards (all should be 0)
        Map<Card.Color, Integer> playedCards = gameManager.getPlayedCards();
        for (Card.Color color : Card.Color.values()) {
            assertEquals(0, playedCards.get(color));
        }
    }

    @Test
    void testTurnManagement() {
        assertEquals("Player1", gameManager.getCurrentPlayerName());
        gameManager.advanceTurn();
        assertEquals("Player2", gameManager.getCurrentPlayerName());
        gameManager.advanceTurn();
        assertEquals("Player3", gameManager.getCurrentPlayerName());
        gameManager.advanceTurn();
        assertEquals("Player1", gameManager.getCurrentPlayerName());
    }

    @Test
    void testPlayCardWrongTurn() {
        // Player2 tries to play when it's Player1's turn
        ActionResult result = gameManager.playCard("Player2", 0);
        assertFalse(result.isSuccess());
        assertEquals("Not your turn or game is over.", result.getMessage());
    }
    
    @Test
    void testGiveHintToSelf() {
        // Player1 tries to give hint to self
        ActionResult result = gameManager.giveHint("Player1", "Player1", HintType.COLOR, Card.Color.RED);
        assertFalse(result.isSuccess());
        assertEquals("Cannot give hint to yourself.", result.getMessage());
    }

    @Test
    void testNoActionAfterGameOver() {
        // Force game over
        gameManager.setGameOver(true);
        
        // Try to perform actions
        ActionResult playResult = gameManager.playCard("Player1", 0);
        assertFalse(playResult.isSuccess());
        
        ActionResult discardResult = gameManager.discardCard("Player1", 0);
        assertFalse(discardResult.isSuccess());
        
        ActionResult hintResult = gameManager.giveHint("Player1", "Player2", HintType.VALUE, 1);
        assertFalse(hintResult.isSuccess());
    }

    @Test
    void testGameStatusForPlayer() {
        GameStatus status = gameManager.getStatusFor("Player1");
        
        assertNotNull(status);
        assertEquals(3, status.getPlayers().size());
        assertEquals("Player1", status.getCurrentPlayer());
        
        // Player1 shouldn't see their own hand in visible hands
        Map<String, List<Card>> visibleHands = status.getVisibleHands();
        assertFalse(visibleHands.containsKey("Player1"));
        assertTrue(visibleHands.containsKey("Player2"));
        assertTrue(visibleHands.containsKey("Player3"));
    }

    @Test
    void testGetPlayerHand() {
        List<Card> hand = gameManager.getPlayerHand("Player1");
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
}