package se2.server.hanabi.game;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.util.GameRules;

class GameStateTest {
    private GameState gameState;
    private final GameLogger logger = new GameLogger();
    private Player player1 = new Player("alice");
    private Player player2 = new Player("bob");
    private Player player3 = new Player("charlie");
    private List<Player> players = List.of(player1, player2, player3);

    private Player playerX = new Player("notInGame");



    @BeforeEach
    void setUp() {
        gameState = new GameState(players, logger);
    }

    @Test
    void testIsCurrentPlayer() {
        assertTrue(gameState.isCurrentPlayer(player1.getId()));
        assertFalse(gameState.isCurrentPlayer(player2.getId()));
        assertFalse(gameState.isCurrentPlayer(playerX.getId()));    
    }

    @Test 
    void testIsActionValid() {
        assertTrue(gameState.isActionValid(player1.getId()));
    }

    @Test 
    void testIsActionValidGameOver() {
        gameState.setGameOver(true);
        assertFalse(gameState.isActionValid(player1.getId()));
    }

    @Test 
    void testIsActionValidNotPLayersTurn() {
        assertFalse(gameState.isActionValid(player2.getId()));
    }

    @Test
    void testPlayerExists() {
        assertTrue(gameState.playerExists(player3.getId()));
        assertFalse(gameState.playerExists(playerX.getId()));
    }

    @Test
    void testGetCardsShowingColorHints() {
        Card.Color cardColor = Card.Color.BLUE;
        ColorHintAndRemainingTurns colorHintAndRemainingTurns = new ColorHintAndRemainingTurns(cardColor, 7);
        int cardId = 18;
        gameState.getCardsShowingColorHintsAndRemainingTurns().put(cardId, colorHintAndRemainingTurns);
        
        assertEquals(cardColor, gameState.getCardsShowingColorHints().get(cardId));
    }

    @Test
    void testGetCardsShowingValueHints() {
        int cardValue = 4;
        ValueHintAndRemainingTurns valueHintAndRemainingTurns = new ValueHintAndRemainingTurns(cardValue, 7);
        int cardId = 18;
        gameState.getCardsShowingValueHintsAndRemainingTurns().put(cardId, valueHintAndRemainingTurns);
        
        assertEquals(cardValue, gameState.getCardsShowingValueHints().get(cardId));
    }

    @Test
    void testAdvanceTurn(){
        assertTrue(gameState.advanceTurn());
        gameState.setGameOver(true);
        assertFalse(gameState.advanceTurn());
    }

    @Test
    void testAdvanceTurnFinalRoundsCounter(){
        gameState.setFinalTurnsRemaining(2);
        gameState.advanceTurn();
        assertEquals(3, gameState.getFinalTurnsRemaining());
    }

    @Test
    void testCheckEndConditionStrikes(){
        gameState.setStrikes(0);
        assertFalse(gameState.checkEndCondition());

        gameState.setStrikes(GameRules.MAX_STRIKES);
        assertTrue(gameState.checkEndCondition());
    }

    @Test 
    void testIsPerfectGame() {
        for (Card.Color color : gameState.getPlayedCards().keySet()) {
            gameState.getPlayedCards().put(color, GameRules.MAX_CARD_VALUE);
        }
        assertTrue(gameState.checkEndCondition());
    }

    @Test 
    void testEndConditionFinalTurnsZero() {
        gameState.setFinalTurnsRemaining(0);
        assertTrue( gameState.checkEndCondition());
    }

    @Test 
    void testSetGameOver() {
        gameState.setGameOver(true);
        assertTrue(gameState.isGameOver());
    }

    @Test
    void testRemoveExpiredShownColorHints() {
        Card.Color cardColor = Card.Color.BLUE;
        ColorHintAndRemainingTurns colorHintAndRemainingTurns = new ColorHintAndRemainingTurns(cardColor, 0);
        int cardId = 18;
        gameState.getCardsShowingColorHintsAndRemainingTurns().put(cardId, colorHintAndRemainingTurns);

        assertFalse(gameState.getCardsShowingColorHintsAndRemainingTurns().isEmpty());
        gameState.removeExpiredShownHints();
        assertTrue(gameState.getCardsShowingColorHintsAndRemainingTurns().isEmpty());
    }

    @Test
    void testRemoveExpiredShownValueHints() {
        int cardValue = 2;
        ValueHintAndRemainingTurns valueHintAndRemainingTurns = new ValueHintAndRemainingTurns(cardValue, 0);
        int cardId = 18;
        gameState.getCardsShowingValueHintsAndRemainingTurns().put(cardId, valueHintAndRemainingTurns);

        assertFalse(gameState.getCardsShowingValueHintsAndRemainingTurns().isEmpty());
        gameState.removeExpiredShownHints();
        assertTrue(gameState.getCardsShowingValueHintsAndRemainingTurns().isEmpty());
    }
}
