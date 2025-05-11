package se2.server.hanabi.game.actions;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.util.GameRules;


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

    @Test
    public void testCompletingStackWithFiveGivesHint() {
        game.setHints(GameRules.MAX_HINTS - 1);
        game.getPlayedCards().put(Card.Color.GREEN, 4);
        List<Card> hand = game.getHands().get("Ermin");
        hand.clear();
        hand.add(new Card(5, Card.Color.GREEN));
        ActionResult result = new PlayCardAction(game, "Ermin", 0).execute();
        assertEquals(GameRules.MAX_HINTS, game.getHints());
    }

    @Test
    public void testPerfectGameEnds() {
        for (Card.Color color : Card.Color.values()) {
            game.getPlayedCards().put(color, GameRules.MAX_CARD_VALUE);
        }
        game.getPlayedCards().put(Card.Color.RED, GameRules.MAX_CARD_VALUE -1);
        List <Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(5, Card.Color.RED));
        ActionResult result = new PlayCardAction(game, "Vlado", 0).execute();
        assertTrue(game.isGameOver());
        assertTrue(result.getMessage().contains("Perfect!"));
    }

    @Test
    public void testGameOverOnThirdStrike() {
        game.incrementStrikes();
        game.incrementStrikes();
        game.incrementStrikes();
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(3, Card.Color.GREEN));
        ActionResult result = new PlayCardAction(game, "Vlado", 0).execute();
        assertTrue(result.getMessage().contains("Game over"));
        assertTrue(game.isGameOver());
    }

    @Test
    public void testPlayCardAfterGameOver() {
        game.setGameOver(true);
        List<Card> hand = game.getHands().get("Vlado");
        hand.clear();
        hand.add(new Card(1, Card.Color.RED));
        ActionResult result = new PlayCardAction(game, "Vlado", 0).execute();
        assertTrue(result.getMessage().contains("Game is already over"));
    }

    @Test
    public void testPlayCardByUnknownPlayer() {
        ActionResult result = new PlayCardAction(game, "Unknown", 0).execute();

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Player not found"));
    }
}
