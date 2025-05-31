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

public class PlayCardActionTest {
    private GameManager game;
    private Player player1 = new Player("alice");
    private Player player2 = new Player("bob");
    private Player player3 = new Player("charli");


    @BeforeEach
    public void setup() {
        game = GameManager.createNewGame(List.of(player1, player2));
    }

    @Test
    public void testCorrectCardAddedToStack() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        List<Card> hand = game.getHands().get(player1.getId());
        hand.clear();
        hand.add(new Card(1, Card.Color.RED));
        ActionResult result = new PlayCardAction(game, player1.getId(), 0).execute();
        assertTrue(result.getMessage().startsWith("You successfully played"));
        assertEquals(1, game.getPlayedCards().get(Card.Color.RED));
    }

    @Test
    public void testInvalidCardCausesStrike() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        List<Card> hand = game.getHands().get(player1.getId());
        hand.clear();
        hand.add(new Card(3, Card.Color.BLUE));
        System.out.println("Before action: Strikes = " + game.getStrikes());
        ActionResult result = new PlayCardAction(game, player1.getId(), 0).execute();
        System.out.println("After action: Strikes = " + game.getStrikes());
        assertTrue(result.getMessage().contains("Wrong card"), "Expected 'Wrong card' message.");
        assertEquals(1, game.getStrikes(), "Strike count should increment to 1.");
    }

    @Test
    public void testCompletingStackWithFiveGivesHint() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        game.setNumRemainingHintTokens(GameRules.MAX_HINT_TOKENS - 1);
        game.getPlayedCards().put(Card.Color.GREEN, 4);
        List<Card> hand = game.getHands().get(player2.getId());
        hand.clear();
        hand.add(new Card(5, Card.Color.GREEN));
        ActionResult result = new PlayCardAction(game, player2.getId(), 0).execute(); 
        assertTrue(result.isSuccess()); // Added assertion to use the result variable
        assertEquals(GameRules.MAX_HINT_TOKENS, game.getHints());
    }

    @Test
    public void testPerfectGameEnds() {
        for (Card.Color color : Card.Color.values()) {
            game.getPlayedCards().put(color, GameRules.MAX_CARD_VALUE);
        }
        game.getPlayedCards().put(Card.Color.RED, GameRules.MAX_CARD_VALUE - 1);

        List<Card> hand = game.getHands().get(player1.getId());
        hand.clear();
        hand.add(new Card(5, Card.Color.RED));

        ActionResult result = new PlayCardAction(game, player1.getId(), 0).execute();
        assertTrue(result.isSuccess(), "Playing the final card should succeed.");
        assertTrue(game.isGameOver(), "Game should be marked as over.");
        assertEquals("Perfect! You completed the game.", result.getMessage(), "Expected message for perfect game.");
    }


    @Test
    public void testPlayCardAfterGameOver() {
        game.setGameOver(true);

        List<Card> hand = game.getHands().get(player1.getId());
        hand.clear();
        hand.add(new Card(1, Card.Color.RED));

        ActionResult result = new PlayCardAction(game, player1.getId(), 0).execute();
        assertFalse(result.isSuccess(), "Playing a card after game over should fail.");
        assertEquals("Game is already over", result.getMessage(), "Expected message for game over scenario.");
    }

    @Test
    public void testPlayCardByUnknownPlayer() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        ActionResult result = new PlayCardAction(game, player3.getId(), 0).execute(); // Using not-in-game player ID 3

        assertFalse(result.isSuccess());
        assertTrue(result.getMessage().contains("Player not found"));
    }

    @Test
    public void testInvalidCardIndexFails() {
        System.out.println("GameManager strikes before action: " + game.getStrikes());
        List<Card> hand = game.getHands().get(player2.getId());
        hand.clear();
        hand.add(new Card(2, Card.Color.YELLOW));

        ActionResult result = new PlayCardAction(game, player2.getId(), 5).execute(); // Passing invalid card index

        assertTrue(result.getMessage().contains("Invalid card index"));
    }

    @Test
    public void testPlayCardWithEmptyDeck() {
        // Simulate an empty deck
        while (!game.getDeck().isEmpty()) {
            game.getDeck().drawCard();
        }

        List<Card> hand = game.getHands().get(player1.getId());
        hand.clear();
        hand.add(new Card(1, Card.Color.RED));

        ActionResult result = new PlayCardAction(game, player1.getId(), 0).execute();
        assertFalse(result.isSuccess(), "Playing a card should fail when the deck is empty.");
        assertEquals("No cards left in the deck.", result.getMessage(), "Expected message for empty deck scenario.");
    }

    @Test
    public void testPlayCardByInvalidPlayer() {
        ActionResult result = new PlayCardAction(game, player3.getId(), 0).execute();
        assertFalse(result.isSuccess(), "Playing a card with an invalid player should fail.");
        assertEquals("Player not found", result.getMessage(), "Expected message for invalid player.");
    }

    @Test
    public void testPlayCardWithInvalidIndex() {
        List<Card> hand = game.getHands().get(player1.getId());
        hand.clear();
        hand.add(new Card(1, Card.Color.RED));

        ActionResult result = new PlayCardAction(game, player1.getId(), -1).execute(); // Invalid index
        assertFalse(result.isSuccess(), "Playing a card with an invalid index should fail.");
        assertEquals("Invalid card index", result.getMessage(), "Expected message for invalid card index.");
    }
}
