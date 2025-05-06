package se2.server.hanabi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DrawServiceTest {
    private DrawService drawService;
    private GameManager gameManager;
    private String playerName;

    @BeforeEach
    void setUp() {
        drawService = new DrawService();
        List<String> players = Arrays.asList("Alice", "Bob");
        gameManager = GameManager.createNewGame(players);
        playerName = "Alice";
    }

    @Test
    void testDrawCardToPlayerHand_success() {
        int initialHandSize = gameManager.getHands().get(playerName).size();
        int initialDeckSize = gameManager.getDeck().getRemainingCards();

        Card card = drawService.drawCardToPlayerHand(gameManager, playerName);

        assertNotNull(card);
        assertEquals(initialHandSize + 1, gameManager.getHands().get(playerName).size());
        assertEquals(initialDeckSize - 1, gameManager.getDeck().getRemainingCards());
    }

    @Test
    void testDrawCardToPlayerHand_deckEmpty() {
        Deck deck = gameManager.getDeck();
        while (!deck.isEmpty()) {
            deck.drawCard();
        }

        int initialHandSize = gameManager.getHands().get(playerName).size();
        Card card = drawService.drawCardToPlayerHand(gameManager, playerName);

        assertNull(card);
        assertEquals(initialHandSize, gameManager.getHands().get(playerName).size());
    }

    @Test
    void testCheckDeckEmptyStatus_setsFinalTurns() {
        Deck deck = gameManager.getDeck();
        while (!deck.isEmpty()) {
            deck.drawCard();
        }

        assertEquals(-1, gameManager.getFinalTurnsRemaining());

        drawService.checkDeckEmptyStatus(gameManager);
        
        assertEquals(gameManager.getPlayers().size(), gameManager.getFinalTurnsRemaining());
    }

    @Test
    void testCheckDeckEmptyStatus_noChangeIfAlreadySet() {
        Deck deck = gameManager.getDeck();
        while (!deck.isEmpty()) {
            deck.drawCard();
        }

        drawService.checkDeckEmptyStatus(gameManager);
        int turns = gameManager.getFinalTurnsRemaining();
        drawService.checkDeckEmptyStatus(gameManager);

        assertEquals(turns, gameManager.getFinalTurnsRemaining());
    }
}
