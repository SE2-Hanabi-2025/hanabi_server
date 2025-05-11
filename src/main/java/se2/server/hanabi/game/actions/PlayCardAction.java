package se2.server.hanabi.game.actions;

import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.GameRules;
import se2.server.hanabi.util.ActionResult;

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
        if (game.isGameOver()){
            return ActionResult.failure("Game is already over");
        }
        // The validation is now handled by GameManager before calling this method
        List<Card> hand = game.getHands().get(playerName);
        if (hand == null ) {
            return ActionResult.failure("Player not found");
        }
        if (cardIndex < 0 || cardIndex >=hand.size()) {
            return ActionResult.failure("Invalid card index");
        }
        Card card = hand.remove(cardIndex);
        int expected = game.getPlayedCards().get(card.getColor()) + 1;

        if (card.getValue() == expected) {
            game.getPlayedCards().put(card.getColor(), expected);
            
            // More detailed success message
            String successMessage = playerName + " successfully played " + card;
            
            // Special message for completing a color stack
            if (card.getValue() == GameRules.MAX_CARD_VALUE) {
                successMessage += " - Completed the " + card.getColor() + " stack!";
                // If player completes a color stack and there are fewer than max hints, add a hint token
                if (game.getHints() < GameRules.MAX_HINTS) {
                    game.setHints(game.getHints() + 1);
                    successMessage += " Gained a hint token!";
                }
            }
            
            game.getLogger().info(successMessage);
            
            // Check if this was the final card for a perfect game
            boolean allComplete = game.getPlayedCards().values().stream()
                .allMatch(value -> value == GameRules.MAX_CARD_VALUE);
            if (allComplete) {
                game.setGameOver(true);
                return ActionResult.success("Perfect! You completed all stacks! Final score: " + game.getCurrentScore());
            }
            
            game.drawCardToHand(playerName);
            game.advanceTurn();
            return ActionResult.success("You successfully played " + card + ". Current score: " + game.getCurrentScore());
        } else {
            game.getDiscardPile().add(card);
            game.incrementStrikes();
            String failMessage = playerName + " played wrong card: " + card + 
                ". Expected " + expected + " of " + card.getColor() + ". Strikes: " + game.getStrikes();
            game.getLogger().warn(failMessage);
            
            if (game.getStrikes() >= GameRules.MAX_STRIKES) {
                game.setGameOver(true);
                game.getLogger().error("Game over: " + playerName + " played the wrong card and reached " + 
                    game.getStrikes() + " strikes. Final score: " + game.getCurrentScore());
                return ActionResult.failure("Wrong card! Game over with a score of " + game.getCurrentScore());
            }
            
            game.drawCardToHand(playerName);
            game.advanceTurn();
            return ActionResult.failure("Wrong card! You played " + card + " but expected " + 
                expected + " of " + card.getColor() + ". You now have " + game.getStrikes() + 
                " strike(s) out of " + GameRules.MAX_STRIKES + ".");
        }
    }
}

