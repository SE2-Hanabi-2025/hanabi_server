package se2.server.hanabi.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.util.GameRules;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameValidatorTest {
    private GameManager gameManager;

    @BeforeEach
    void setUp() {
        gameManager = mock(GameManager.class);
    }

    @Test
    void testIsPlayerTurn_True() {
        when(gameManager.isGameOver()).thenReturn(false);
        when(gameManager.getCurrentPlayerName()).thenReturn("Alice");
        assertTrue(GameValidator.isPlayerTurn(gameManager, "Alice"));
    }

    @Test
    void testIsPlayerTurn_False_GameOver() {
        when(gameManager.isGameOver()).thenReturn(true);
        when(gameManager.getCurrentPlayerName()).thenReturn("Alice");
        assertFalse(GameValidator.isPlayerTurn(gameManager, "Alice"));
    }

    @Test
    void testIsPlayerTurn_False_WrongPlayer() {
        when(gameManager.isGameOver()).thenReturn(false);
        when(gameManager.getCurrentPlayerName()).thenReturn("Bob");
        assertFalse(GameValidator.isPlayerTurn(gameManager, "Alice"));
    }

    @Test
    void testIsValidCardIndex_Valid() {
        List<Card> hand = Arrays.asList(mock(Card.class), mock(Card.class));
        Map<String, List<Card>> hands = new HashMap<>();
        hands.put("Alice", hand);
        when(gameManager.getHands()).thenReturn(hands);
        assertTrue(GameValidator.isValidCardIndex(gameManager, "Alice", 1));
    }

    @Test
    void testIsValidCardIndex_InvalidIndex() {
        List<Card> hand = Arrays.asList(mock(Card.class));
        Map<String, List<Card>> hands = new HashMap<>();
        hands.put("Alice", hand);
        when(gameManager.getHands()).thenReturn(hands);
        
        assertFalse(GameValidator.isValidCardIndex(gameManager, "Alice", 1));
        assertFalse(GameValidator.isValidCardIndex(gameManager, "Alice", -1));
        assertFalse(GameValidator.isValidCardIndex(gameManager, "Alice", 2));
        assertTrue(GameValidator.isValidCardIndex(gameManager, "Alice", 0));
    }
    
    @Test
    void testIsValidCardIndex_ExactBoundaries() {
        List<Card> hand = Arrays.asList(mock(Card.class), mock(Card.class));
        Map<String, List<Card>> hands = new HashMap<>();
        hands.put("Alice", hand);
        when(gameManager.getHands()).thenReturn(hands);
        
        assertTrue(GameValidator.isValidCardIndex(gameManager, "Alice", 0));
        assertTrue(GameValidator.isValidCardIndex(gameManager, "Alice", 1));
        assertFalse(GameValidator.isValidCardIndex(gameManager, "Alice", 2));
    }

    @Test
    void testIsValidCardIndex_NoHand() {
        Map<String, List<Card>> hands = new HashMap<>();
        when(gameManager.getHands()).thenReturn(hands);
        assertFalse(GameValidator.isValidCardIndex(gameManager, "Bob", 0));
    }

    @Test
    void testPlayerExists_True() {
        Player alice = mock(Player.class);
        when(alice.getName()).thenReturn("Alice");
        Player bob = mock(Player.class);
        when(bob.getName()).thenReturn("Bob");
        when(gameManager.getPlayers()).thenReturn(Arrays.asList(alice, bob));
        assertTrue(GameValidator.playerExists(gameManager, "Bob"));
    }

    @Test
    void testPlayerExists_False() {
        Player alice = mock(Player.class);
        when(alice.getName()).thenReturn("Alice");
        when(gameManager.getPlayers()).thenReturn(Collections.singletonList(alice));
        assertFalse(GameValidator.playerExists(gameManager, "Charlie"));
    }

    @Test
    void testHasEnoughHints_True() {
        when(gameManager.getHints()).thenReturn(2);
        assertTrue(GameValidator.hasEnoughHints(gameManager));
    }

    @Test
    void testHasEnoughHints_False() {
        when(gameManager.getHints()).thenReturn(0);
        assertFalse(GameValidator.hasEnoughHints(gameManager));
    }

    @Test
    void testCanDiscard_True() {
        when(gameManager.getHints()).thenReturn(GameRules.MAX_HINTS - 1);
        assertTrue(GameValidator.canDiscard(gameManager));
    }

    @Test
    void testCanDiscard_False() {
        when(gameManager.getHints()).thenReturn(GameRules.MAX_HINTS);
        assertFalse(GameValidator.canDiscard(gameManager));
    }

    @Test
    void testIsValidHintTypeAndValue_Color() {
        assertTrue(GameValidator.isValidHintTypeAndValue(HintType.COLOR, Card.Color.RED));
        assertFalse(GameValidator.isValidHintTypeAndValue(HintType.COLOR, 1));
    }

    @Test
    void testIsValidHintTypeAndValue_Value() {
        assertTrue(GameValidator.isValidHintTypeAndValue(HintType.VALUE, GameRules.MIN_CARD_VALUE));
        assertFalse(GameValidator.isValidHintTypeAndValue(HintType.VALUE, 0));
        assertFalse(GameValidator.isValidHintTypeAndValue(HintType.VALUE, "not a number"));
    }

    @Test
    void testIsNotSelfHint() {
        assertTrue(GameValidator.isNotSelfHint("Alice", "Bob"));
        assertFalse(GameValidator.isNotSelfHint("Alice", "Alice"));
    }
}
