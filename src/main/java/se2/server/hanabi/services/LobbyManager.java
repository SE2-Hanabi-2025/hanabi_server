package se2.server.hanabi.services;

import org.springframework.stereotype.Service;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Lobby;
import se2.server.hanabi.model.Player;

import java.util.*;

@Service
public class LobbyManager {

    private final Map<String, Lobby> lobbies = new HashMap<>();

    public String createLobby() {
        String uniqueCode = generateUniqueCode();
        Lobby lobby = new Lobby(uniqueCode);
        lobbies.put(lobby.getId(), lobby);
        return uniqueCode;
    }

    private String generateUniqueCode() {
        String code;
        do {
            code = LobbyCodeGenerator.generateLobbyCode();
        } while (lobbies.containsKey(code));
        return code;
    }

    public Lobby getLobby(String id) {
        return lobbies.get(id);
    }

    public int joinLobby(String id, String playerName, int avatarResID) {
        Lobby lobby = lobbies.get(id);
        if (lobby == null || lobby.isGameStarted()) {
            return -1;
        }

        List<Player> players = lobby.getPlayers();
        if (players.size() >= 5) {
            return -1;
        }

        Player newPlayer = new Player(playerName, avatarResID);
        players.add(newPlayer);
        return newPlayer.getId();
    }

    public Collection<Lobby> getAllLobbies() {
        return lobbies.values();
    }
    
    /**
     * Start the game for a specific lobby
     * @param lobbyId the ID of the lobby
     * @param isCasaulMode sets the game mode
     * @Parameter(name = "name", description = "The name of the player joining the lobby", required = false, in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "Anonymous"),
     * @return true if game was successfully started, false otherwise
     */
    public boolean startGame(String lobbyId, Boolean isCasaulMode) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null || lobby.isGameStarted()) {
            return false;
        }
        
        return lobby.startGame(isCasaulMode);
    }

    /**
     * Start the game for a specific lobby
     * @param lobbyId the ID of the lobby
     * @Parameter(name = "name", description = "The name of the player joining the lobby", required = false, in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "Anonymous"),
     * @return true if game was successfully started, false otherwise
     */
    public boolean startGame(String lobbyId) {
        return startGame(lobbyId, false);
    }
    
    /**
     * Get the GameManager for a specific lobby
     * @param lobbyId the ID of the lobby
     * @return the GameManager or null if lobby doesn't exist or game hasn't started
     */
    public GameManager getGameManager(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null || !lobby.isGameStarted()) {
            return null;
        }
        
        return lobby.getGameManager();
    }
    
    /**
     * Remove a lobby
     * @param lobbyId the ID of the lobby to remove
     * @return true if lobby was removed, false if it didn't exist
     */
    public boolean removeLobby(String lobbyId) {
        return lobbies.remove(lobbyId) != null;
    }

    public boolean leaveLobby(String lobbyId, int playerId){
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null){
            return false;
        }
        return lobby.removePlayerId(playerId);
    }

}


