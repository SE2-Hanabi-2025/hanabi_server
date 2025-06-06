package se2.server.hanabi.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameStatusTest {

    private List<Player> players;
    private Map<Integer, List<Card>> visibleHands; // Changed key to Integer for playerId
    private Map<Card.Color, Integer> playedCards;
    private List<Card> discardPile;
    private int hints;
    private int strikes;
    private boolean gameOver;
    private int currentPlayer; // Changed to Integer for playerId

    private GameStatus gameStatus;

    @BeforeEach
    void setUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        when(player1.getId()).thenReturn(1); // Mock player IDs
        when(player2.getId()).thenReturn(2);
        players = Arrays.asList(player1, player2);

        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);
        List<Card> hand1 = Arrays.asList(card1, card2);
        visibleHands = new HashMap<>();
        visibleHands.put(1, hand1); // Using playerId as key

        playedCards = new EnumMap<>(Card.Color.class);
        playedCards.put(Card.Color.RED, 2);
        playedCards.put(Card.Color.BLUE, 1);

        discardPile = Arrays.asList(card1);

        hints = 5;
        strikes = 1;
        gameOver = false;
        currentPlayer = 1; // Using playerId

        gameStatus = new GameStatus(players, visibleHands, playedCards, discardPile, hints, strikes, gameOver, String.valueOf(currentPlayer));
    }

    @Test
    void testGetPlayers() {
        assertEquals(players, gameStatus.getPlayers());
    }

    @Test
    void testGetVisibleHands() {
        assertEquals(visibleHands, gameStatus.getVisibleHands());
    }

    @Test
    void testGetPlayedCards() {
        assertEquals(playedCards, gameStatus.getPlayedCards());
    }

    @Test
    void testGetDiscardPile() {
        assertEquals(discardPile, gameStatus.getDiscardPile());
    }

    @Test
    void testGetHints() {
        assertEquals(hints, gameStatus.getHints());
    }

    @Test
    void testGetStrikes() {
        assertEquals(strikes, gameStatus.getStrikes());
    }

    @Test
    void testIsGameOver() {
        assertEquals(gameOver, gameStatus.isGameOver());
    }

    @Test
    void testGetCurrentPlayer() {
        assertEquals(String.valueOf(currentPlayer), gameStatus.getCurrentPlayer());
    }

    @Test
    void testGameStatusWithGameOverTrue() {
        GameStatus status = new GameStatus(players, visibleHands, playedCards, discardPile, hints, strikes, true, String.valueOf(currentPlayer));
        assertTrue(status.isGameOver());
    }
}