package se2.server.hanabi.game.actions;

import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.GameRules;
import se2.server.hanabi.util.ActionResult;

import java.util.List;

public class PlayCardAction {
    private final GameManager game;
    private final int playerId;
    private final int cardIndex;

    public PlayCardAction(GameManager game, int playerId, int cardIndex) {
        this.game = game;
        this.playerId = playerId;
        this.cardIndex = cardIndex;
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
        if (cardIndex < 0 || cardIndex >= hand.size()) {
            game.getLogger().warn("Invalid card index " + cardIndex + " by player " + playerId);
            return ActionResult.failure("Invalid card index");
        }
        Card card = hand.remove(cardIndex);
        game.getLogger().info("Player " + playerId + " played card: " + card);
        int expected = game.getPlayedCards().get(card.getColor()) + 1;

        game.getLogger().info("Card value: " + card.getValue() + ", Expected: " + expected);

        if (card.getValue() != expected) {
            game.getLogger().warn("Player " + playerId + " played an invalid card: " + card);
            game.getDiscardPile().add(card);
            game.incrementStrikes(); // Ensure strikes are incremented for invalid cards
            game.getLogger().warn("Wrong card played by player " + playerId);
            game.drawCardToHand(playerId);
            game.advanceTurn();
            return ActionResult.failure("Wrong card!");
        }

        if (card.getValue() == expected) {
            game.getPlayedCards().put(card.getColor(), expected);
            game.getLogger().info("Played cards state: " + game.getPlayedCards());
            if (card.getValue() == GameRules.MAX_CARD_VALUE && game.getHints() < GameRules.MAX_HINTS) {
                game.setNumRemainingHintTokens(game.getHints() + 1);
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

            game.removeCardFromShownHints(card.getId());
            game.drawCardToHand(playerId);
            game.advanceTurn();
            return ActionResult.success("You successfully played " + card);
        }

        // Ensure a default return statement for valid card plays
        return ActionResult.success("Card played successfully.");
    }
}

