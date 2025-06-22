package se2.server.hanabi.model;

import lombok.Getter;
import se2.server.hanabi.game.GameManager;
import java.util.*;


@Getter
public class Lobby {

    private final String id;

    private final List<Player> players;
    
    private boolean isGameStarted;
    
    private GameManager gameManager;

    public Lobby(String id) {
        this.id = id;
        this.players = new ArrayList<>();
        this.isGameStarted = false;
    }

    /**
     * Start the game by creating a GameManager instance with the current players
     * @param isCasualMode sets the game mode
     * @return true if game was successfully started, false otherwise
     */
    public boolean startGame(Boolean isCasualMode) {
        if (isGameStarted || players.size() < 2) {
            return false;
        }

        // Create a new GameManager with the players
        this.gameManager = GameManager.createNewGame(players, isCasualMode);

        this.isGameStarted = true;
        return true;
    }

    /**
     * Start the game by creating a GameManager instance with the current players
     * @return true if game was successfully started, false otherwise
     */
    public boolean startGame() {
        return startGame(false);
    }

    public boolean removePlayerId(int playerId){
        return this.players.removeIf(player -> player.getId() == playerId);
    }
}
