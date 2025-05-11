package se2.server.hanabi.game.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.game.HintType;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.ActionResult;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HintActionTest {

    private GameManager game;

    @BeforeEach
    public void setup() {
        game = GameManager.createNewGame(List.of("Vlado", "Ermin"));
    }

    @Test
    public void testValidColorHintDecreasesTokens() {
        game.setHints(4);
        List<Card> hand = game.getHands().get("Ermin");
        hand.clear();
        hand.add(new Card(2, Card.Color.BLUE));
        ActionResult result = new HintAction(game, "Vlado", "Ermin", HintType.COLOR, Card.Color.BLUE).execute();
        assertTrue(result.getMessage().contains("Hint given"));
        assertEquals(3, game.getHints());
    }

    @Test
    public void testValidValueHintDecreasesTokens() {
        game.setHints(2);
        List<Card> hand = game.getHands().get("Ermin");
        hand.clear();
        hand.add(new Card(4, Card.Color.RED));
        ActionResult result = new HintAction(game, "Vlado", "Ermin", HintType.VALUE, 4).execute();
        assertTrue(result.getMessage().contains("Hint given"));
        assertEquals(1, game.getHints());

    }

    @Test
    public void testHintCannotBeGivenToSelf() {
        game.setHints(2);
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(1, Card.Color.WHITE));
        ActionResult result = new HintAction(game, "Vlado", "Vlado", HintType.COLOR, Card.Color.WHITE).execute();
        assertTrue(result.getMessage().contains("Cannot give hint to yourself"));
        assertEquals(2, game.getHints());
    }

    @Test
    public void testHintFailsWithNoTokens() {
        game.setHints(0);
        List<Card> hand = game.getHands().get("Ermin");
        hand.clear();
        hand.add(new Card(3, Card.Color.GREEN));
        ActionResult result = new HintAction(game, "Vlado", "Ermin", HintType.COLOR, Card.Color.GREEN).execute();
        assertTrue(result.getMessage().contains("No hint tokens available"));
        assertEquals(0, game.getHints());
    }

    @Test
    public void testHintWithNoMatchingCardsStillWorks() {
        game.setHints(2);
        List<Card> hand = game.getHands().get("Ermin");
        hand.clear();
        hand.add(new Card(5, Card.Color.YELLOW));
        ActionResult result = new HintAction(game, "Vlado", "Ermin", HintType.VALUE, 3).execute();
        assertTrue(result.getMessage().contains("Hint given"));
        assertEquals(1, game.getHints());
    }
}
