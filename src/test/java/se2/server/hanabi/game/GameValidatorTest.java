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
        when(gameManager.getCurrentPlayerId()).thenReturn(1);
        assertTrue(GameValidator.isPlayerTurn(gameManager, 1));
    }

    @Test
    void testIsPlayerTurn_False_GameOver() {
        when(gameManager.isGameOver()).thenReturn(true);
        when(gameManager.getCurrentPlayerId()).thenReturn(1);
        assertFalse(GameValidator.isPlayerTurn(gameManager, 1));
    }

    @Test
    void testIsPlayerTurn_False_WrongPlayer() {
        when(gameManager.isGameOver()).thenReturn(false);
        when(gameManager.getCurrentPlayerId()).thenReturn(2);
        assertFalse(GameValidator.isPlayerTurn(gameManager, 1));
    }

    @Test
    void testIsValidCardIndex_Valid() {
        List<Card> hand = new ArrayList<Card>();
        Map<Integer, List<Card>> hands = new HashMap<>();
        hands.put(1, hand);
        Card card = new Card(4, Card.Color.RED);
        hands.get(1).add(card);
        when(gameManager.getHands()).thenReturn(hands);
        assertTrue(GameValidator.isValidCardId(gameManager, 1, card.getId()));
    }

    @Test
    void testIsValidCardIndex_InvalidIndex() {
        List<Card> hand = new ArrayList<Card>();
        Map<Integer, List<Card>> hands = new HashMap<>();
        hands.put(1, hand);
        Card card = new Card(4, Card.Color.RED);
        hands.get(1).add(card);
        when(gameManager.getHands()).thenReturn(hands);

        assertFalse(GameValidator.isValidCardId(gameManager, 1, card.getId()+1));
        assertFalse(GameValidator.isValidCardId(gameManager, 1, -1));
        assertTrue(GameValidator.isValidCardId(gameManager, 1, card.getId()));
    }

    @Test
    void testIsValidCardIndex_NoHand() {
        Map<Integer, List<Card>> hands = new HashMap<>();
        when(gameManager.getHands()).thenReturn(hands);
        assertFalse(GameValidator.isValidCardId(gameManager, 2, 0));
    }

    @Test
    void testPlayerExists_True() {
        Player alice = mock(Player.class);
        when(alice.getId()).thenReturn(1);
        Player bob = mock(Player.class);
        when(bob.getId()).thenReturn(2);
        when(gameManager.getPlayers()).thenReturn(Arrays.asList(alice, bob));
        assertTrue(GameValidator.playerExists(gameManager, 2));
    }

    @Test
    void testPlayerExists_False() {
        Player alice = mock(Player.class);
        when(alice.getId()).thenReturn(1);
        when(gameManager.getPlayers()).thenReturn(Collections.singletonList(alice));
        assertFalse(GameValidator.playerExists(gameManager, 3));
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
        assertTrue(GameValidator.isNotSelfHint(1, 2));
        assertFalse(GameValidator.isNotSelfHint(1, 1));
    }
}
