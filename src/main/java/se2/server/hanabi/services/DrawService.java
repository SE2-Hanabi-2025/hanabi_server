package se2.server.hanabi.services;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;
import se2.server.hanabi.gamemanager.GameManager;

/**
 * Service handling all card drawing operations in the game
 */
public class DrawService {
    
    /**
     * Draws a card from the deck and adds it to the player's hand if possible
     * 
     * @param gameManager the game manager containing the game state
     * @param playerName the name of the player drawing a card
     * @return the drawn card or null if no card could be drawn
     */
    public Card drawCardToPlayerHand(GameManager gameManager, String playerName) {
        Deck deck = gameManager.getDeck();
        
        if (deck.isEmpty()) {
            gameManager.getLogger().info(playerName + " could not draw a card - deck is empty.");
            return null;
        }
        
        Card card = deck.drawCard();
        if (card != null) {
            gameManager.getHands().get(playerName).add(card);
            gameManager.getLogger().info(playerName + " drew a new card. " + 
                deck.getRemainingCards() + " cards left in deck.");
        }
        
        return card;
    }
    
    /**
     * Checks if the deck is empty and updates the final turns counter if needed
     * 
     * @param gameManager the game manager to check
     */
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