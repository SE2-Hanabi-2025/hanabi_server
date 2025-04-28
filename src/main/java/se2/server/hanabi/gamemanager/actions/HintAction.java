package se2.server.hanabi.gamemanager.actions;

import se2.server.hanabi.gamemanager.GameManager;
import se2.server.hanabi.gamemanager.ActionResult;
import se2.server.hanabi.gamemanager.HintType;

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

        // TODO: Apply filtering logic to hint receiver's hand

        game.setHints(game.getHints() - 1);
        game.getLogger().info(fromPlayer + " gave a hint to " + toPlayer + ": " + type + " " + value);

        game.advanceTurn();
        return ActionResult.success("Hint given to " + toPlayer);
    }
}

