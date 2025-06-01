package se2.server.hanabi.game.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.game.HintType;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.util.ActionResult;

import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class HintActionTest {

    private GameManager game;
    private Player player1 = new Player("alice");
    private Player player2 = new Player("bob");
    private Player player3 = new Player("charli");

    @BeforeEach
    public void setup() {

        game = GameManager.createNewGame(List.of(player1, player2));
    }

    @Test
    public void testValidColorHintDecreasesTokens() {
        game.setNumRemainingHintTokens(4);
        List<Card> hand = game.getHands().get(player2.getId());
        hand.clear();
        hand.add(new Card(2, Card.Color.BLUE));
        ActionResult result = new HintAction(game, player1.getId(), player2.getId(), HintType.COLOR, Card.Color.BLUE).execute(); // Passing player IDs 1 and 2
        assertTrue(result.getMessage().contains("Hint given"));
        assertEquals(3, game.getHints());
    }

    @Test
    public void testValidValueHintDecreasesTokens() {
        game.setNumRemainingHintTokens(2);
        List<Card> hand = game.getHands().get(player2.getId()); // Using player ID 2
        hand.clear();
        hand.add(new Card(4, Card.Color.RED));
        ActionResult result = new HintAction(game, player1.getId(), player2.getId(), HintType.VALUE, 4).execute(); // Passing player IDs 1 and 2
        assertTrue(result.getMessage().contains("Hint given"));
        assertEquals(1, game.getHints());

    }

    @Test
    public void testHintCannotBeGivenToSelf() {
        game.setNumRemainingHintTokens(2);
        List<Card> hand = game.getHands().get(player1.getId()); // Using player ID 1
        hand.clear();
        hand.add(new Card(1, Card.Color.WHITE));

        ActionResult result = game.giveHint(player1.getId(), player1.getId(), HintType.COLOR, Card.Color.WHITE); // Using GameManager.giveHint

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Cannot give hint to yourself"));
        assertEquals(2, game.getHints()); // Hint tokens should remain unchanged
    }

    @Test
    public void testHintFailsWithNoTokens() {
        game.setNumRemainingHintTokens(0);
        List<Card> hand = game.getHands().get(player2.getId()); // Using player ID 2
        hand.clear();
        hand.add(new Card(3, Card.Color.GREEN));

        ActionResult result = game.giveHint(player1.getId(), player2.getId(), HintType.COLOR, Card.Color.GREEN); // Using GameManager.giveHint

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("No hint tokens available"));
        assertEquals(0, game.getHints()); // Hint tokens should remain unchanged
    }

    @Test
    public void testHintWithNoMatchingCardsStillFails() {
        game.setNumRemainingHintTokens(2);
        List<Card> hand = game.getHands().get(player2.getId()); // Using player ID 2
        hand.clear();
        hand.add(new Card(5, Card.Color.YELLOW));

        ActionResult result = game.giveHint(player1.getId(), player2.getId(), HintType.VALUE, 3); // Using GameManager.giveHint

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("No matching cards found"));
        assertEquals(2, game.getHints()); // Hint tokens should remain unchanged
    }

    @Test
    public void testGiveHintToInvalidPlayer() {
        ActionResult result = new HintAction(game, player1.getId(), player3.getId(), HintType.VALUE, 3).execute(); // Using GameManager.giveHint

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Target player not found"));
        assertEquals(8, game.getHints()); // Hint tokens should remain full
    }
}
