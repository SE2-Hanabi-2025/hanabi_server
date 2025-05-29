package se2.server.hanabi.game.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.util.GameRules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardCardActionTest {
    private GameManager game;

    @BeforeEach
    public void setup() {
        Player player1 = new Player("alice");
        Player player2 = new Player("bob");
        game = GameManager.createNewGame(List.of(player1, player2));
    }

    @Test
    public void testDiscardingCardAddsToDiscardPile() {
        List<Card> hand = game.getHands().get(1); // Using player ID 1
        hand.clear();
        hand.add(new Card(2, Card.Color.GREEN));
        ActionResult result = new DiscardCardAction(game, 1, 0).execute(); // Passing player ID 1
        assertTrue(result.getMessage().contains("Card discarded"));
        assertEquals(1, game.getDiscardPile().size());
    }

    @Test
    public void testDiscardByNonexistentPlayer() {
        ActionResult result = new DiscardCardAction(game, 3, 0).execute(); // Using non-existent player ID 3
        assertTrue(result.getMessage().contains("Player not found"));
    }

    @Test
    public void testDiscardAfterGameOver() {
        game.setGameOver(true);
        List<Card> hand = game.getHands().get(1); // Using player ID 1
        hand.clear();
        hand.add(new Card(3, Card.Color.RED));
        ActionResult result = new DiscardCardAction(game, 1, 0).execute(); // Passing player ID 1
        assertTrue(result.getMessage().contains("Game is already over"));
    }

    @Test
    public void testDiscardingIncreasesHintTokens() {
        game.setNumRemainingHintTokens(GameRules.MAX_HINTS - 1);
        List<Card> hand = game.getHands().get(1); // Using player ID 1
        hand.clear();
        hand.add(new Card(4, Card.Color.BLUE));
        new DiscardCardAction(game, 1, 0).execute(); // Passing player ID 1
        assertEquals(GameRules.MAX_HINTS, game.getHints());
    }

    @Test
    public void testDiscardDoesNotExceedMaxHintTokens() {
        game.setNumRemainingHintTokens(GameRules.MAX_HINTS);
        List<Card> hand = game.getHands().get(1); // Using player ID 1
        hand.clear();
        hand.add(new Card(4, Card.Color.WHITE));
        new DiscardCardAction(game, 1, 0).execute(); // Passing player ID 1
        assertEquals(GameRules.MAX_HINTS, game.getHints());
    }

    @Test
    public void testDiscardWithInvalidIndex() {
        List<Card> hand = game.getHands().get(1); // Using player ID 1
        hand.clear();
        hand.add(new Card(1, Card.Color.WHITE));
        ActionResult result = new DiscardCardAction(game, 1, 5).execute(); // Passing invalid card index
        assertTrue(result.getMessage().contains("Invalid card index"));
    }
}
