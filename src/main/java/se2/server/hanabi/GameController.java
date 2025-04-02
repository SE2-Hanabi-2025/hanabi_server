package se2.server.hanabi;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import se2.server.hanabi.CardLogic.Card;
import se2.server.hanabi.CardLogic.Deck;

@RestController
public class GameController {
    private Deck deck = new Deck();  

    
    @GetMapping("/connect")
    public String connect() {
        return "Connection established with the server!";
    }

    @GetMapping("/start-game")
    public String startGame() {
        deck = new Deck();  // Reset the deck
        return "Game started! New deck created.";
    }

    @GetMapping("/draw")
    public String drawCard() {
        Card card = deck.drawCard();
        return (card != null) ? "Drew a card: " + card : "No more cards in the deck!";
    }

}