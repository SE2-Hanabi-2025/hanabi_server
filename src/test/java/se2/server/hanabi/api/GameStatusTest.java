package se2.server.hanabi.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameStatusTest {    private List<Player> players;
    private List<Integer> playersHand; // New field for player's hand as integers
    private Map<Integer, List<Card>> visibleHands; // Changed key to Integer for playerId
    private Map<Card.Color, Integer> playedCards;
    private List<Card> discardPile;
    private int numRemainingCard; // New field for remaining cards
    private Map<Integer, Object> shownHints; // New field for shown hints
    private int hintTokens; // Renamed from hints
    private int strikes;
    private boolean gameOver;
    private int currentPlayer; // Changed to Integer for playerId

    private GameStatus gameStatus;    @BeforeEach
    void setUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        when(player1.getId()).thenReturn(1); // Mock player IDs
        when(player2.getId()).thenReturn(2);
        players = Arrays.asList(player1, player2);

        // Mock player's hand as list of integers (card IDs)
        playersHand = Arrays.asList(1, 2, 3); // Sample card IDs

        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);
        List<Card> hand1 = Arrays.asList(card1, card2);
        visibleHands = new HashMap<>();
        visibleHands.put(1, hand1); // Using playerId as key

        playedCards = new EnumMap<>(Card.Color.class);
        playedCards.put(Card.Color.RED, 2);
        playedCards.put(Card.Color.BLUE, 1);

        discardPile = Arrays.asList(card1);

        numRemainingCard = 25; // Sample remaining cards count
        shownHints = new HashMap<>(); // Empty hints map for testing
        hintTokens = 5; // Renamed from hints
        strikes = 1;
        gameOver = false;
        currentPlayer = 1; // Using playerId

        gameStatus = new GameStatus(players, playersHand, visibleHands, playedCards, discardPile, numRemainingCard, shownHints, hintTokens, strikes, gameOver, currentPlayer);
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
    }    @Test
    void testGetHintTokens() {
        assertEquals(hintTokens, gameStatus.getHintTokens());
    }

    @Test
    void testGetPlayersHand() {
        assertEquals(playersHand, gameStatus.getPlayersHand());
    }

    @Test
    void testGetNumRemainingCard() {
        assertEquals(numRemainingCard, gameStatus.getNumRemainingCard());
    }

    @Test
    void testGetShownHints() {
        assertEquals(shownHints, gameStatus.getShownHints());
    }

    @Test
    void testGetStrikes() {
        assertEquals(strikes, gameStatus.getStrikes());
    }

    @Test
    void testIsGameOver() {
        assertEquals(gameOver, gameStatus.isGameOver());
    }    @Test
    void testGetCurrentPlayer() {
        assertEquals(currentPlayer, gameStatus.getCurrentPlayer());
    }

    @Test
    void testGameStatusWithGameOverTrue() {
        GameStatus status = new GameStatus(players, playersHand, visibleHands, playedCards, discardPile, numRemainingCard, shownHints, hintTokens, strikes, true, currentPlayer);
        assertTrue(status.isGameOver());
    }
}