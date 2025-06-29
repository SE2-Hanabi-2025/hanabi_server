package se2.server.hanabi.game.actions;

import se2.server.hanabi.game.ColorHintAndRemainingTurns;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.game.HintType;
import se2.server.hanabi.game.ValueHintAndRemainingTurns;
import se2.server.hanabi.model.Card;

import java.util.ArrayList;
import java.util.List;

public class HintAction {
    private final GameManager game;
    private final int fromPlayerId;
    private final int toPlayerId;
    private final HintType type;
    private final Object value;

    public HintAction(GameManager game, int fromPlayerId, int toPlayerId, HintType type, Object value) {
        this.game = game;
        this.fromPlayerId = fromPlayerId;
        this.toPlayerId = toPlayerId;
        this.type = type;
        this.value = value;
    }

    public ActionResult execute() {
        List<Card> targetHand = game.getHands().get(toPlayerId);
        if (targetHand == null) {
            game.getLogger().warn("Target player " + toPlayerId + " not found while giving hint");
            return ActionResult.failure("Target player not found");
        }

        List<Integer> matchingCardIds = new ArrayList<>();
        for (Card card : targetHand) {
            if (type == HintType.COLOR && card.getColor().name().equalsIgnoreCase(value.toString())) {
                ColorHintAndRemainingTurns colorHintAndTurns = new ColorHintAndRemainingTurns(card.getColor(), game.getNumTurnsHintsLast());
                game.getGameState().getCardsShowingColorHintsAndRemainingTurns().put(card.getId(), colorHintAndTurns);
                matchingCardIds.add(card.getId());
            } else if (type == HintType.VALUE && card.getValue() == (int) value) {
                ValueHintAndRemainingTurns valueHintAndTurns = new ValueHintAndRemainingTurns(card.getValue(), game.getNumTurnsHintsLast());
                game.getGameState().getCardsShowingValueHintsAndRemainingTurns().put(card.getId(), valueHintAndTurns);
                matchingCardIds.add(card.getId());
            }
        }

        if (matchingCardIds.isEmpty()) {
            game.getLogger().info("No matching cards found for hint by player " + fromPlayerId);
            return ActionResult.failure("No matching cards found");
        }
        
        StringBuilder logMessage = new StringBuilder("Player " + fromPlayerId + " gave a hint to player " + toPlayerId + " about " + type + " " + value + ". Matching cards: ");
        
        for (int i = 0; i < targetHand.size(); i++) {
            Card card = targetHand.get(i);
            if (matchingCardIds.contains(card.getId())) {
                logMessage.append("[Position: ").append(i).append(", ID: ").append(card.getId())
                          .append(", Value: ").append(card.getValue())
                          .append(", Color: ").append(card.getColor().name()).append("] ");
            }
        }

    
        
        game.getLogger().info(logMessage.toString());
        game.setNumRemainingHintTokens(game.getHints() - 1); // Deduct hint tokens only when a valid hint is applied
        game.advanceTurn();
        return ActionResult.success("Hint given");
    }
}

