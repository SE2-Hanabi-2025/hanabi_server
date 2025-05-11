package se2.server.hanabi.game;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.util.GameRules;
import java.util.List;

public class GameValidator {
    public static boolean isPlayerTurn(GameManager game, int playerId) {
        return !game.isGameOver() && game.getCurrentPlayerId() == playerId;
    }

    public static boolean isValidCardIndex(GameManager game, int playerId, int cardIndex) {
        List<Card> hand = game.getHands().get(playerId);
        return hand != null && cardIndex >= 0 && cardIndex < hand.size();
    }

    public static boolean playerExists(GameManager game, int playerId) {
        return game.getPlayers().stream().anyMatch(p -> p.getId() == playerId);
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

    public static boolean isNotSelfHint(int fromPlayerId, int toPlayerId) {
        return fromPlayerId != toPlayerId;
    }
}