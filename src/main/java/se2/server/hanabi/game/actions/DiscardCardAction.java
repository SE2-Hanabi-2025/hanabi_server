package se2.server.hanabi.game.actions;

import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.ActionResult;

import java.util.List;

public class DiscardCardAction {
    private final GameManager game;
    private final int playerId;
    private final int cardIndex;

    public DiscardCardAction(GameManager game, int playerId, int cardIndex) {
        this.game = game;
        this.playerId = playerId;
        this.cardIndex = cardIndex;
    }

    public ActionResult execute() {
        if (game.isGameOver()) {
            game.getLogger().error("Discard failed - Game is already over.");
            return ActionResult.failure("Game is already over");
        }
        // The validation is now handled by GameManager before calling this method
        List<Card> hand = game.getHands().get(playerId);
        if (hand == null) {
            game.getLogger().error("Discard failed - Unknown player");
            return ActionResult.failure("Player not found");
        }
        if (cardIndex < 0 || cardIndex >= hand.size()) {
            game.getLogger().error("Discard failed - Invalid card index " + cardIndex + " for player: " + playerId);
            return ActionResult.failure("Invalid card index");
        }
        Card card = hand.remove(cardIndex);
        game.getDiscardPile().add(card);
        game.setNumRemainingHintTokens(game.getHints() + 1);
        game.removeCardFromShownHints(card.getId());
        game.getLogger().info("Player " + playerId + " discarded " + card + " and gained a hint.");

        game.drawCardToHand(playerId);
        game.advanceTurn();
        return ActionResult.success("Card discarded.");
    }
}

