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

        if (card.getValue() == expected) {
            game.getPlayedCards().put(card.getColor(), expected);
            if (card.getValue() == GameRules.MAX_CARD_VALUE && game.getHints() < GameRules.MAX_HINTS) {
                game.setHints(game.getHints() + 1);
            }
            game.drawCardToHand(playerId);
            game.advanceTurn();
            return ActionResult.success("You successfully played " + card);
        } else {
            game.getDiscardPile().add(card);
            game.incrementStrikes();
            if (game.getStrikes() >= GameRules.MAX_STRIKES) {
                game.setGameOver(true);
                game.getLogger().error("Wrong card played by player " + playerId + ". Game over.");
                return ActionResult.failure("Wrong card! Game over.");
            }
            game.getLogger().warn("Wrong card played by player " + playerId);
            game.drawCardToHand(playerId);
            game.advanceTurn();
            return ActionResult.failure("Wrong card!");
        }
    }
}

