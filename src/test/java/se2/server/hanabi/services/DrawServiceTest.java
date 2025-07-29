package se2.server.hanabi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;
import se2.server.hanabi.model.Player;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DrawServiceTest {
    private DrawService drawService;
    private GameManager gameManager;
    private int playerId;

    @BeforeEach
    void setUp() {
        drawService = new DrawService();
        Player player1 = new Player("alice");
        Player player2 = new Player("bob");
        gameManager = GameManager.createNewGame(List.of(player1, player2));
        playerId = player1.getId();
    }

    @Test
    void testDrawCardToPlayerHand_success() {
        int initialHandSize = gameManager.getHands().get(playerId).size();
        int initialDeckSize = gameManager.getDeck().getNumRemainingCards();

        Card card = drawService.drawCardToPlayerHand(gameManager, playerId);

        assertNotNull(card);
        assertEquals(initialHandSize + 1, gameManager.getHands().get(playerId).size());
        assertEquals(initialDeckSize - 1, gameManager.getDeck().getNumRemainingCards());
    }

    @Test
    void testDrawCardToPlayerHand_deckEmpty() {
        Deck deck = gameManager.getDeck();
        while (!deck.isEmpty()) {
            deck.drawCard();
        }

        int initialHandSize = gameManager.getHands().get(playerId).size();
        Card card = drawService.drawCardToPlayerHand(gameManager, playerId);

        assertNull(card);
        assertEquals(initialHandSize, gameManager.getHands().get(playerId).size());
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
