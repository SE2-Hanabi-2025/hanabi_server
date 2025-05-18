package se2.server.hanabi.game.actions;

import org.assertj.core.data.MapEntry;
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
    private List<Integer> playerIds;

    @BeforeEach
    public void setup() {
        playerIds = List.of(1, 2);
        game = GameManager.createNewGame(playerIds); // Using player IDs instead of names
    }

    @Test
    public void testCorrectCardAddedToStack() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        List<Card> hand = game.getHands().get(1); // Using player ID 1
        hand.clear();
        Card card = new Card(1, Card.Color.RED);
        hand.add(card);
        ActionResult result = new PlayCardAction(game, 1, card.getId(), card.getColor()).execute(); // Passing player ID 1
        assertTrue(result.getMessage().startsWith("You successfully played"));
        assertEquals(1, game.getPlayedCards().get(Card.Color.RED));
    }

    @Test
    public void testInvalidCardCausesStrike() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        List<Card> hand = game.getHands().get(1); // Using player ID 1
        hand.clear();
        Card card = new Card(3, Card.Color.BLUE);
        hand.add(card);
        System.out.println("Before action: Strikes = " + game.getStrikes());
        ActionResult result = new PlayCardAction(game, 1, card.getId(), card.getColor()).execute(); // Passing player ID 1
        System.out.println("After action: Strikes = " + game.getStrikes());
        assertTrue(result.getMessage().contains("Wrong card"), "Expected 'Wrong card' message.");
        assertEquals(1, game.getStrikes(), "Strike count should increment to 1.");
    }

    @Test
    public void testCompletingStackWithFiveGivesHint() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        game.setHints(GameRules.MAX_HINTS - 1);
        game.getPlayedCards().put(Card.Color.GREEN, 4);
        List<Card> hand = game.getHands().get(2); // Using player ID 2
        hand.clear();
        Card card = new Card(5, Card.Color.GREEN);
        hand.add(card);
        ActionResult result = new PlayCardAction(game, 2, card.getId(), card.getColor()).execute(); // Passing player ID 2
        assertTrue(result.isSuccess()); // Added assertion to use the result variable
        assertEquals(GameRules.MAX_HINTS, game.getHints());
    }

    @Test
    public void testPerfectGameEnds() {
        for (Card.Color color : Card.Color.values()) {
            game.getPlayedCards().put(color, GameRules.MAX_CARD_VALUE);
        }
        game.getPlayedCards().put(Card.Color.RED, GameRules.MAX_CARD_VALUE - 1);

        List<Card> hand = game.getHands().get(1);
        hand.clear();
        Card card = new Card(5, Card.Color.RED);
        hand.add(card);

        ActionResult result = new PlayCardAction(game, 1, card.getId(), card.getColor()).execute();
        assertTrue(result.isSuccess(), "Playing the final card should succeed.");
        assertTrue(game.isGameOver(), "Game should be marked as over.");
        assertEquals("Perfect! You completed the game.", result.getMessage(), "Expected message for perfect game.");
    }


    @Test
    public void testPlayCardAfterGameOver() {
        game.setGameOver(true);

        List<Card> hand = game.getHands().get(1);
        hand.clear();
        hand.add(new Card(1, Card.Color.RED));

        ActionResult result = new PlayCardAction(game, 1, 0, Card.Color.YELLOW).execute();
        assertFalse(result.isSuccess(), "Playing a card after game over should fail.");
        assertEquals("Game is already over", result.getMessage(), "Expected message for game over scenario.");
    }

    @Test
    public void testPlayCardByUnknownPlayer() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        ActionResult result = new PlayCardAction(game, 3, 0, Card.Color.BLUE).execute(); // Using non-existent player ID 3

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Player not found"));
    }

    @Test
    public void testIncorrectCardIdFails() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        List<Card> hand = game.getHands().get(2); // Using player ID 2
        hand.clear();
        Card card =new Card(2, Card.Color.YELLOW);
        hand.add(card);

        ActionResult result = new PlayCardAction(game, 2, card.getId()+1, Card.Color.BLUE).execute(); // Passing invalid card id

        assertTrue(result.getMessage().contains("Incorrect card id"));
    }

    @Test
    public void testPlayCardWithEmptyDeck() {
        // Simulate an empty deck
        while (!game.getDeck().isEmpty()) {
            game.getDeck().drawCard();
        }

        List<Card> hand = game.getHands().get(1);
        hand.clear();
        Card card =new Card(1, Card.Color.RED);
        hand.add(card);

        ActionResult result = new PlayCardAction(game, 1, card.getId(), card.getColor()).execute();
        assertFalse(result.isSuccess(), "Playing a card should fail when the deck is empty.");
        assertEquals("No cards left in the deck.", result.getMessage(), "Expected message for empty deck scenario.");
    }

    @Test
    public void testPlayCardByInvalidPlayer() {
        ActionResult result = new PlayCardAction(game, 99, 0, Card.Color.RED).execute(); // Invalid player ID
        assertFalse(result.isSuccess(), "Playing a card with an invalid player should fail.");
        assertEquals("Player not found", result.getMessage(), "Expected message for invalid player.");
    }

    @Test
    public void testPlayRightNumberWrongColor() {
        game.getPlayedCards().remove(Card.Color.RED);
        game.getPlayedCards().put(Card.Color.RED, 3);

        int playerId = playerIds.get(0);
        Card card = new Card(1, Card.Color.BLUE);
        game.getHands().get(playerId).add(card);

        ActionResult result = new PlayCardAction(game, playerId, card.getId(), Card.Color.RED).execute();
        assertFalse(result.isSuccess(), "PLaying a card which does have a valid placemnet but on the wrong stack should fail");
    }
}
