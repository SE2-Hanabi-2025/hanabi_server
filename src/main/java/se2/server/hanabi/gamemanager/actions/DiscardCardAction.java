package se2.server.hanabi.gamemanager.actions;

import se2.server.hanabi.gamemanager.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.rules.GameRules;
import se2.server.hanabi.gamemanager.ActionResult;

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
        // The validation is now handled by GameManager before calling this method
        List<Card> hand = game.getHands().get(playerName);
        Card card = hand.remove(cardIndex);
        game.getDiscardPile().add(card);
        game.setHints(game.getHints() + 1);
        game.getLogger().info(playerName + " discarded " + card + " and gained a hint.");

        game.drawCardToHand(playerName);
        game.advanceTurn();
        return ActionResult.success("Card discarded.");
    }
}

