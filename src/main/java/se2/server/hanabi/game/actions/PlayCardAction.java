package se2.server.hanabi.game.actions;

import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.GameRules;
import se2.server.hanabi.util.ActionResult;

import java.util.List;

public class PlayCardAction {
    private final GameManager game;
    private final int playerId;
    private final int cardId;
    private final Card.Color stackColor;

    public PlayCardAction(GameManager game, int playerId, int cardId, Card.Color stackColor) {
        this.game = game;
        this.playerId = playerId;
        this.cardId = cardId;
        this.stackColor = stackColor;
    }

    public ActionResult execute() {
        if (game.isGameOver()) {
            game.getLogger().warn("Attempt to play card after game over by player " + playerId);
            return ActionResult.failure("Game is already over");
        }

        List<Card> hand = game.getHands().get(playerId);
        if (hand == null) {
            game.getLogger().warn("Player " + playerId + " not found while playing card");
            return ActionResult.failure("Player not found");
        }

        Card selectedCard = null;
        for (Card card : hand) {
            if (cardId == card.getId()) {
                selectedCard = card;
                break;
            }
        }
        if (selectedCard==null) {
            game.getLogger().warn("Invalid card id " + cardId + " by player " + playerId);
            return ActionResult.failure("Incorrect card id");
        }

        hand.remove(selectedCard);
        game.getLogger().info("Player " + playerId + " played card: " + selectedCard);
        int expectedValue = game.getPlayedCards().get(stackColor) + 1;

        game.getLogger().info("Card: " + selectedCard.getColor()+" "+selectedCard.getValue() + ", Expected: " + stackColor + " "+ expectedValue);

        if (selectedCard.getValue() != expectedValue || selectedCard.getColor() != stackColor) {
            game.getLogger().warn("Player " + playerId + " played an invalid card: " + selectedCard);
            game.getDiscardPile().add(selectedCard);
            game.incrementStrikes(); // Ensure strikes are incremented for invalid cards
            game.getLogger().warn("Wrong card played by player " + playerId);
            game.drawCardToHand(playerId);
            game.advanceTurn();
            return ActionResult.failure("Wrong card!");
        }

        // correct card played
        game.getPlayedCards().put(selectedCard.getColor(), selectedCard.getValue());
        game.getLogger().info("Played cards state: " + game.getPlayedCards());
        if (selectedCard.getValue() == GameRules.MAX_CARD_VALUE && game.getHints() < GameRules.MAX_HINTS) {
            game.setHints(game.getHints() + 1);
        }

        // Check if all cards are played perfectly
        boolean allPerfect = game.getPlayedCards().values().stream()
            .allMatch(value -> value == GameRules.MAX_CARD_VALUE);
        if (allPerfect) {
            game.setGameOver(true);
            game.getLogger().info("Perfect game achieved! Game over.");
            return ActionResult.success("Perfect! You completed the game.");
        }

        if (game.getDeck().isEmpty()) {
            game.getLogger().warn("Deck is empty. No card drawn.");
            game.advanceTurn();
            return ActionResult.failure("No cards left in the deck.");
        }

        game.drawCardToHand(playerId);
        game.advanceTurn();
        return ActionResult.success("You successfully played " + selectedCard);
    }
}

