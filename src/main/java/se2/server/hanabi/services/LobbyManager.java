package se2.server.hanabi.services;

import org.springframework.stereotype.Service;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Lobby;
import se2.server.hanabi.model.Player;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class LobbyManager {

    private final Map<String, Lobby> lobbies = new ConcurrentHashMap<>();

    public LobbyManager(){
    }

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
    
    public boolean startGame(String lobbyId, Boolean isCasaulMode) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null || lobby.isGameStarted()) {
            return false;
        }
        
        return lobby.startGame(isCasaulMode);
    }

    public boolean startGame(String lobbyId) {
        return startGame(lobbyId, false);
    }
    
    public GameManager getGameManager(String lobbyId) {
        Lobby lobby = lobbies.get(lobbyId);
        if (lobby == null || !lobby.isGameStarted()) {
            return null;
        }
        
        return lobby.getGameManager();
    }
    
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

    public Optional<String> ReturnToGame(int playerId){
        for (Lobby lobby : lobbies.values()){
            GameManager gameManager = lobby.getGameManager();
            if (gameManager != null && !gameManager.isGameOver()){
                if (lobby.getPlayerId(playerId) != null){
                    return Optional.of(lobby.getId());
            }
        }
    }
    return Optional.empty();
}
}


