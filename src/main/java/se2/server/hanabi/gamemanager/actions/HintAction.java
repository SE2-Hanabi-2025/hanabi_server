package se2.server.hanabi.gamemanager.actions;

import se2.server.hanabi.gamemanager.GameManager;
import se2.server.hanabi.gamemanager.ActionResult;
import se2.server.hanabi.gamemanager.HintType;
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
        if (!game.getCurrentPlayerName().equals(fromPlayer)) {
            return ActionResult.invalid("Not your turn.");
        }
        if (game.getHints() == 0) {
            return ActionResult.invalid("No hints left.");
        }

        var targetHand = game.getHands().get(toPlayer);
        if (targetHand == null) {
            return ActionResult.invalid("Target player not found.");
        }

        // TODO: Apply filtering logic to hint receiver's hand
        List<Integer> matchingIndices = new ArrayList<>();
        for (int i = 0; i < targetHand.size(); i++) {
            Card card = targetHand.get(i);
            if ((type == HintType.COLOR && card.getColor().name().equalsIgnoreCase(value.toString())) ||
                    (type == HintType.VALUE && card.getValue() == (int) value)) {
                matchingIndices.add(i);
            }
        }

        if (matchingIndices.isEmpty()) {
            return ActionResult.invalid("No matching cards found for this hint.");
        }


        game.setHints(game.getHints() - 1);
        game.getLogger().info(fromPlayer + " gave a hint to " + toPlayer + ": " + type + " " + value + " (matches at positions " + matchingIndices + ").");

        game.advanceTurn();
        return ActionResult.success("Hint given to " + toPlayer);
    }
}

