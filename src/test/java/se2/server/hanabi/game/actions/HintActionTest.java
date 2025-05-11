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
        asserEquals(3, game.getHints());
    }
}
