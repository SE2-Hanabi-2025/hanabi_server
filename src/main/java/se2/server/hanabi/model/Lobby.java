package se2.server.hanabi.model;

import io.swagger.v3.oas.annotations.Operation;
import lombok.Getter;
import se2.server.hanabi.game.GameManager;
import java.util.*;
import java.util.List;
import java.util.Objects;

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

    public Player disconnectedPlayer(int playerId){
        Player player = getPlayerId(playerId);
        if (player != null){
            player.setStatus(Player.ConnectionStatus.DISCONNECTED);
        }
        return player;
    }

    public boolean removePlayerId(int playerId){
        return this.players.removeIf(p -> p.getId() == playerId);
    }

    public Player getPlayerId(int playerId){
        for (Player player : this.players){
            if (player.getId() == playerId){
                return player;
            }
        }
        return null;
    }

    @Override

    public boolean equals(Object o){
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Lobby lobby = (Lobby) o;
        return Objects.equals(id, lobby.id);
    }

    @Override
    public int hashCode(){
        return Objects.hash(id);
    }
}
