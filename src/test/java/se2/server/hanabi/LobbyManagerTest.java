 package se2.server.hanabi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.model.Lobby;
import se2.server.hanabi.services.LobbyManager;

import static org.junit.jupiter.api.Assertions.*;

 public class LobbyManagerTest {

    private LobbyManager lobbyManager;

    @BeforeEach
    void setUp() {
        lobbyManager = new LobbyManager();
    }

    @Test
    void createLobby_ReturnNotNullId() {
        String lobbyId = lobbyManager.createLobby();
        assertNotNull(lobbyId, "Lobby ID should not be null");
    }

    @Test
    void createLobby_ReturnUniqueLobbyId() {
        String lobbyId1 = lobbyManager.createLobby();
        String lobbyId2 = lobbyManager.createLobby();
        assertNotEquals(lobbyId1, lobbyId2, "Lobby IDs should be unique");
    }

    @Test
    void joinLobby_WhenFull() {
        String lobbyId = lobbyManager.createLobby();
        Lobby lobby = lobbyManager.getLobby(lobbyId);

        for (int i = 0; i < 5; i++) {
            lobbyManager.joinLobby(lobbyId, "Player" + i);
        }

        boolean result = lobbyManager.joinLobby(lobbyId, "Player6");

        assertFalse(result, "Lobby should not allow more than 5 players");
    }

    @Test
    void joinLobby_WhenGameStarted() {
        String lobbyId = lobbyManager.createLobby();
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        lobby.startGame(); // Spiel starten

        boolean result = lobbyManager.joinLobby(lobbyId, "NewPlayer");

        assertFalse(result, "No player should be able to join once the game has started");
    }

    @Test
    void joinLobby_SuccessfulJoin() {
        String lobbyId = lobbyManager.createLobby();
        boolean result = lobbyManager.joinLobby(lobbyId, "Player1");

        assertTrue(result, "Player should be able to join the lobby");
    }

    @Test
    void getAllLobbies_ReturnsLobbies() {
        String lobbyId1 = lobbyManager.createLobby();
        String lobbyId2 = lobbyManager.createLobby();

        assertEquals(2, lobbyManager.getAllLobbies().size(), "There should be 2 lobbies");
    }

}