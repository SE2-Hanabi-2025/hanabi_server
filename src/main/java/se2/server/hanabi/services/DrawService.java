package se2.server.hanabi.services;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;
import se2.server.hanabi.game.GameManager;

/**
 * Service handling all card drawing operations in the game
 */
public class DrawService {
    
    public Card drawCardToPlayerHand(GameManager gameManager, int playerId) {
        Deck deck = gameManager.getDeck();
        
        if (deck.isEmpty()) {
            gameManager.getLogger().info("Player " + playerId + " could not draw a card - deck is empty.");
            return null;
        }
        
        Card card = deck.drawCard();
        if (card != null) {
            gameManager.getHands().get(playerId).add(card);
            gameManager.getLogger().info("Player " + playerId + " drew a new card. " + 
                deck.getNumRemainingCards() + " cards left in deck.");
        }
        
        return card;
    }

    public void checkDeckEmptyStatus(GameManager gameManager) {
        Deck deck = gameManager.getDeck();
        
        if (deck.isEmpty() && gameManager.getFinalTurnsRemaining() == -1) {
            int playerCount = gameManager.getPlayers().size();
            gameManager.setFinalTurnsRemaining(playerCount);
            gameManager.getLogger().info("Deck is empty. Final round started! " + 
                gameManager.getFinalTurnsRemaining() + " turns remaining.");
        }
    }
}