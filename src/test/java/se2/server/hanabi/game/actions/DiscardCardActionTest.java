package se2.server.hanabi.game.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.ActionResult;

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
}
