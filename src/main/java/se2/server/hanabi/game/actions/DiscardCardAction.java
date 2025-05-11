package se2.server.hanabi.game.actions;

import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.ActionResult;

import java.util.List;

public class DiscardCardAction {
    private final GameManager game;
    private final String playerName;
    private final int cardIndex;

    public DiscardCardAction(GameManager game, String playerName, int cardIndex) {
        this.game = game;
        this.playerName = playerName;
        this.cardIndex = cardIndex;
    }

    public ActionResult execute() {
        if (game.isGameOver()) {
            game.getLogger().error("Discard failed - Game is already over.");
            return ActionResult.failure("Game is already over");
        }
        // The validation is now handled by GameManager before calling this method
        List<Card> hand = game.getHands().get(playerName);
        if (hand == null) {
            game.getLogger().error("Discard failed - Unknown player");
            return ActionResult.failure("Player not found");
        }
        if (cardIndex < 0 || cardIndex >= hand.size()) {
            game.getLogger().error("Discard failed - Invalid card index" + cardIndex + " for player: " + playerName);
            return ActionResult.failure("Invalid card index");
        }
        Card card = hand.remove(cardIndex);
        game.getDiscardPile().add(card);
        game.setHints(game.getHints() + 1);
        game.getLogger().info(playerName + " discarded " + card + " and gained a hint.");

        game.drawCardToHand(playerName);
        game.advanceTurn();
        return ActionResult.success("Card discarded.");
    }
}

