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
        if (!game.getCurrentPlayerName().equals(playerName)) {
            return ActionResult.invalid("Not your turn.");
        }

        if (game.getHints() >= GameRules.MAX_HINTS) {
            return ActionResult.invalid("Hint tokens are full.");
        }

        List<Card> hand = game.getHands().get(playerName);
        if (cardIndex < 0 || cardIndex >= hand.size()) {
            return ActionResult.invalid("Invalid card index.");
        }

        Card card = hand.remove(cardIndex);
        game.getDiscardPile().add(card);
        game.setHints(game.getHints() + 1);
        game.drawCardToHand(playerName);
        game.advanceTurn();
        return ActionResult.success("Card discarded.");
    }
}

