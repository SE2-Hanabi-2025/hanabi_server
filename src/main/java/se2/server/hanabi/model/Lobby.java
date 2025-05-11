package se2.server.hanabi.model;

import lombok.Getter;
import se2.server.hanabi.game.GameManager;

import java.util.*;

public class Lobby {

    @Getter
    private final String id;

    @Getter
    private List<Player> players = new ArrayList<>();
    
    private boolean isGameStarted;
    
    @Getter
    private GameManager gameManager;

    public Lobby(String id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.isGameStarted = false;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }
    
    /**
     * Start the game by creating a GameManager instance with the current players
     * @return true if game was successfully started, false otherwise
     */
    public boolean startGame() {
        if (isGameStarted || players.size() < 2) {
            return false;
        }
        
        // Create a new GameManager with the current players
        this.gameManager = GameManager.createNewGame(players);
        
        this.isGameStarted = true;
        return true;
    }
}
