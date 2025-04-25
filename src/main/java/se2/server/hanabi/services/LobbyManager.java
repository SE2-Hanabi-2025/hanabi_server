package se2.server.hanabi.services;

import org.springframework.stereotype.Service;
import se2.server.hanabi.model.Lobby;
import se2.server.hanabi.model.Player;

import java.util.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class LobbyManager {

    private final Map<String, Lobby> lobbies = new HashMap<>();

    public String  createLobby() {
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

    public boolean joinLobby(String id, String playerName) {
        Lobby lobby = lobbies.get(id);
        if (lobby == null || lobby.isGameStarted()) {
            return false;
        }

        List<Player> players = lobby.getPlayers();
        if (players.size() >= 5) {
            return false;
        }

        players.add(new Player(playerName));
        return true;
    }

    public Collection<Lobby> getAllLobbies() {
        return lobbies.values();
    }
}


