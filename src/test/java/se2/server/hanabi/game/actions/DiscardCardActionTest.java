package se2.server.hanabi.game.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.util.GameRules;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class DiscardCardActionTest {
    private GameManager game;

    @BeforeEach
    public void setup() {
        game = GameManager.createNewGame(List.of("Vlado", "Ermin"));
    }

    @Test
    public void testDiscardingCardAddsToDiscardPile() {
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(2, Card.Color.GREEN));
        ActionResult result = new DiscardCardAction(game, "Vlado", 0).execute();
        assertTrue(result.getMessage().contains("Card discarded"));
        assertEquals(1, game.getDiscardPile().size());
    }

    @Test
    public void testDiscardByNonexistentPlayer() {
        ActionResult result = new DiscardCardAction(game, "Ena", 0).execute();
        assertTrue(result.getMessage().contains("Player not found"));
    }

    @Test
    public void testDiscardAfterGameOver() {
        game.setGameOver(true);
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(3, Card.Color.RED));
        ActionResult result = new DiscardCardAction(game, "Vlado", 0).execute();
        assertTrue(result.getMessage().contains("Game is already over"));
    }

    @Test
    public void testDiscardingIncreasesHintTokens() {
        game.setHints(GameRules.MAX_HINTS - 1);
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(4, Card.Color.BLUE));
        new DiscardCardAction(game, "Vlado", 0).execute();
        assertEquals(GameRules.MAX_HINTS, game.getHints());
    }

    @Test
    public void testDiscardDoesNotExceedMaxHintTokens() {
        game.setHints(GameRules.MAX_HINTS);
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(4, Card.Color.WHITE));
        new DiscardCardAction(game, "Vlado", 0).execute();
        assertEquals(GameRules.MAX_HINTS, game.getHints());
    }

    @Test
    public void testDiscardWithInvalidIndex() {
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(1, Card.Color.WHITE));
        ActionResult result = new DiscardCardAction(game, "Vlado", 5).execute();
        assertTrue(result.getMessage().contains("Invalid card index"));
    }
}
