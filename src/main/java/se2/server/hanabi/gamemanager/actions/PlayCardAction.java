package se2.server.hanabi.gamemanager.actions;

import se2.server.hanabi.gamemanager.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.rules.GameRules;
import se2.server.hanabi.gamemanager.ActionResult;

import java.util.List;

public class PlayCardAction {
    private final GameManager game;
    private final String playerName;
    private final int cardIndex;

    public PlayCardAction(GameManager game, String playerName, int cardIndex) {
        this.game = game;
        this.playerName = playerName;
        this.cardIndex = cardIndex;
    }

    public ActionResult execute() {
        if (!game.getCurrentPlayerName().equals(playerName)) {
            return ActionResult.invalid("Not your turn.");
        }

        List<Card> hand = game.getHands().get(playerName);
        if (cardIndex < 0 || cardIndex >= hand.size()) {
            return ActionResult.invalid("Invalid card index.");
        }

        Card card = hand.remove(cardIndex);
        int expected = game.getPlayedCards().get(card.getColor()) + 1;

        if (card.getValue() == expected) {
            game.getPlayedCards().put(card.getColor(), expected);
        } else {
            game.getDiscardPile().add(card);
            game.incrementStrikes();
            if (game.getStrikes() >= GameRules.MAX_STRIKES) {
                game.setGameOver(true);
                return ActionResult.failure("Wrong card. Game over!");
            }
        }

        game.drawCardToHand(playerName);
        game.advanceTurn();
        return ActionResult.success("Card played.");
    }
}

