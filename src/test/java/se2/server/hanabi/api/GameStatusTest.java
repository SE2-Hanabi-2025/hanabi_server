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
    private List<Integer> playerCardIds;
    private Map<Integer, List<Card>> visibleHands; // Changed key to Integer for playerId
    private Map<Card.Color, Integer> playedCards;
    private List<Card> discardPile;
    private int numRemaningCards;
    private final Map<Integer, Card.Color> cardsShowingColorHints = new HashMap<Integer, Card.Color>();
    private final Map<Integer, Integer> cardsShowingValueHints = new HashMap<Integer, Integer>();
    private int numRemainingHintTokens;
    private int strikes;
    private boolean gameOver;
    private boolean gameLost;
    private int currentScore;
    private int currentPlayerId;

    private GameStatus gameStatus;    @BeforeEach
    void setUp() {
        Player player1 = mock(Player.class);
        Player player2 = mock(Player.class);
        when(player1.getId()).thenReturn(1); // Mock player IDs
        when(player2.getId()).thenReturn(2);
        players = Arrays.asList(player1, player2);

        // Mock player's hand as list of integers (card IDs)
        playerCardIds = Arrays.asList(1, 2, 3); // Sample card IDs

        Card card1 = mock(Card.class);
        Card card2 = mock(Card.class);
        List<Card> hand1 = Arrays.asList(card1, card2);
        visibleHands = new HashMap<>();
        visibleHands.put(1, hand1); // Using playerId as key

        playedCards = new EnumMap<>(Card.Color.class);
        playedCards.put(Card.Color.RED, 2);
        playedCards.put(Card.Color.BLUE, 1);

        discardPile = Arrays.asList(card1);

        playerCardIds = new ArrayList<Integer>();
        playerCardIds.add(1);
        playerCardIds.add(5);
        numRemaningCards = 17;

        int colorHintId = 1;
        Card.Color hintColor = Card.Color.YELLOW;
        cardsShowingColorHints.put(colorHintId, hintColor);

        int valueHintId = 1;
        int hintValue = 4;
        cardsShowingValueHints.put(valueHintId , hintValue);

        numRemainingHintTokens = 5;
        strikes = 1;
        gameOver = false;
        gameLost = false;
        currentScore = 17;
        currentPlayerId = 1; // Using playerId

        List<Card> ownHand = new ArrayList<>(); // Add this for the new constructor
        gameStatus = new GameStatus(players, playerCardIds, visibleHands, playedCards, discardPile, numRemaningCards, cardsShowingColorHints, cardsShowingValueHints, numRemainingHintTokens, strikes, gameOver, gameLost, currentScore, currentPlayerId, ownHand);
    }

    @Test
    void testGetPlayers() {
        assertEquals(players, gameStatus.getPlayers());
    }

    @Test
    void testGetPlayerCardIds() {
        assertEquals(playerCardIds, gameStatus.getPlayerCardIds());
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
    void testGetNumRemainingCards() {
        assertEquals(numRemaningCards, gameStatus.getNumRemainingCards());
    }

    @Test
    void testGetCardsShowingColorHints() {
        assertEquals(cardsShowingColorHints, gameStatus.getCardsShowingColorHints());
    }

    @Test
    void testGetCardsShowingValueHints() {
        assertEquals(cardsShowingValueHints, gameStatus.getCardsShowingValueHints());
    }

    @Test
    void testGetNumRemainingHintTokens() {
        assertEquals(numRemainingHintTokens, gameStatus.getNumRemainingHintTokens());
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
    void testIsGameLost() {
        assertEquals(gameLost, gameStatus.isGameLost());
    }  

    @Test
    void testGetCurrentScore() {
        assertEquals(currentScore, gameStatus.getCurrentScore());
    }  
    
    @Test
    void testGetCurrentPlayer() {
        assertEquals(currentPlayerId, gameStatus.getCurrentPlayerId());
    }

    @Test
    void testGameStatusWithGameOverTrue() {
        List<Card> ownHand = new ArrayList<>(); // Add this for the new constructor
        GameStatus status = new GameStatus(players, playerCardIds, visibleHands, playedCards, discardPile, numRemaningCards, cardsShowingColorHints, cardsShowingValueHints, numRemainingHintTokens, strikes, true, gameLost, currentScore, currentPlayerId, ownHand);
        assertTrue(status.isGameOver());
    }
}