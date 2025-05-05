package se2.server.hanabi.gamemanager;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.rules.GameRules;
import java.util.List;
import java.util.Map;

public class GameValidator {
    public static boolean isPlayerTurn(GameManager game, String playerName) {
        return !game.isGameOver() && game.getCurrentPlayerName().equals(playerName);
    }

    public static boolean isValidCardIndex(GameManager game, String playerName, int cardIndex) {
        List<Card> hand = game.getHands().get(playerName);
        return hand != null && cardIndex >= 0 && cardIndex < hand.size();
    }

    public static boolean playerExists(GameManager game, String playerName) {
        return game.getPlayers().stream().anyMatch(p -> p.getName().equals(playerName));
    }

    public static boolean hasEnoughHints(GameManager game) {
        return game.getHints() > 0;
    }

    public static boolean canDiscard(GameManager game) {
        return game.getHints() < GameRules.MAX_HINTS;
    }

    public static boolean isValidHintTypeAndValue(HintType type, Object value) {
        if (type == HintType.COLOR) {
            return value instanceof Card.Color;
        } else if (type == HintType.VALUE) {
            return value instanceof Integer && GameRules.isValidCardValue((Integer) value);
        }
        return false;
    }

    public static boolean isNotSelfHint(String fromPlayer, String toPlayer) {
        return !fromPlayer.equals(toPlayer);
    }
}