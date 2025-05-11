package se2.server.hanabi.game.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.ActionResult;


import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class PlayCardActionTest {
    private GameManager game;

    @BeforeEach
    public void setup() {
        game = GameManager.createNewGame(List.of("Vlado", "Ermin"));
    }

    @Test
    public void testCorrectCardAddedToStack() {
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(1, Card.Color.RED));
        ActionResult result = new PlayCardAction(game, "Vlado", 0).execute();
        assertTrue(result.getMessage().startsWith("You successfully played"));
        assertEquals(1, game.getPlayedCards().get(Card.Color.RED));
    }

    @Test
    public void testInvalidCardCausesStrike() {
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(3, Card.Color.BLUE));
        ActionResult result = new PlayCardAction(game, "Vlado", 0).execute();
        assertTrue(result.getMessage().contains("Wrong card"));
        assertEquals(1, game.getStrikes());
    }
}
