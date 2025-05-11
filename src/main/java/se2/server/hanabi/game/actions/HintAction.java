package se2.server.hanabi.game.actions;

import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.game.HintType;
import se2.server.hanabi.model.Card;

import java.util.ArrayList;
import java.util.List;

public class HintAction {
    private final GameManager game;
    private final String fromPlayer;
    private final String toPlayer;
    private final HintType type;
    private final Object value;

    public HintAction(GameManager game, String from, String to, HintType type, Object value) {
        this.game = game;
        this.fromPlayer = from;
        this.toPlayer = to;
        this.type = type;
        this.value = value;
    }

    public ActionResult execute() {
        // All validation is now handled by GameManager
        var targetHand = game.getHands().get(toPlayer);
        
        // Apply filtering logic to hint receiver's hand
        List<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < targetHand.size(); i++) {
            Card card = targetHand.get(i);
            if ((type == HintType.COLOR && card.getColor().name().equalsIgnoreCase(value.toString())) ||
                    (type == HintType.VALUE && card.getValue() == (int) value)) {
                matchingIndices.add(i);
            }
        }

        if (matchingIndices.isEmpty()) {
            game.setHints(game.getHints() - 1);
            game.getLogger().info(fromPlayer + " gave a hint to " + toPlayer + ": " + type + " " + value + " (matches at positions " + matchingIndices + ")");
            game.advanceTurn();
            return ActionResult.success("Hint given");
        }
        if (fromPlayer.equals(toPlayer)) {
            game.getLogger().error("Hint failed - player tried to hint themselves");
            return ActionResult.failure("Cannot give hint to yourself");
        }
        if (game.getHints() <= 0) {
            game.getLogger().error("Hint failed - no hint tokens available");
            return ActionResult.failure("No hint tokens available");
        }

        game.setHints(game.getHints() - 1);
        game.getLogger().info(fromPlayer + " gave a hint to " + toPlayer + ": " + type + " " + value + " (matches at positions " + matchingIndices + ").");

        game.advanceTurn();
        return ActionResult.success("Hint given to " + toPlayer);
    }
}

