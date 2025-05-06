package se2.server.hanabi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.model.Lobby;

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
        
        // Add the minimum required players to start a game (2 players)
        lobbyManager.joinLobby(lobbyId, "Player1");
        lobbyManager.joinLobby(lobbyId, "Player2");
        
        // Start the game properly through the lobbyManager
        boolean gameStarted = lobbyManager.startGame(lobbyId);
        assertTrue(gameStarted, "Game should start successfully with 2 players");

        // Now try to join after game is started
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

    @Test
    void getLobby_ReturnsCorrectLobby() {
        String lobbyId = lobbyManager.createLobby();
        Lobby lobby = lobbyManager.getLobby(lobbyId);
        assertNotNull(lobby, "Should return the created lobby");
        assertEquals(lobbyId, lobby.getId(), "Lobby ID should match");
    }

    @Test
    void getLobby_ReturnsNullForNonexistentLobby() {
        Lobby lobby = lobbyManager.getLobby("nonexistent");
        assertNull(lobby, "Should return null for nonexistent lobby");
    }

    @Test
    void startGame_ReturnsFalseIfLobbyDoesNotExist() {
        boolean result = lobbyManager.startGame("invalidId");
        assertFalse(result, "Should return false if lobby does not exist");
    }

    @Test
    void startGame_ReturnsFalseIfGameAlreadyStarted() {
        String lobbyId = lobbyManager.createLobby();
        lobbyManager.joinLobby(lobbyId, "Player1");
        lobbyManager.joinLobby(lobbyId, "Player2");
        assertTrue(lobbyManager.startGame(lobbyId), "First startGame should succeed");
        assertFalse(lobbyManager.startGame(lobbyId), "Second startGame should fail");
    }

    @Test
    void getGameManager_ReturnsNullIfLobbyDoesNotExist() {
        assertNull(lobbyManager.getGameManager("invalidId"), "Should return null if lobby does not exist");
    }

    @Test
    void getGameManager_ReturnsNullIfGameNotStarted() {
        String lobbyId = lobbyManager.createLobby();
        assertNull(lobbyManager.getGameManager(lobbyId), "Should return null if game not started");
    }

    @Test
    void getGameManager_ReturnsGameManagerIfGameStarted() {
        String lobbyId = lobbyManager.createLobby();
        lobbyManager.joinLobby(lobbyId, "Player1");
        lobbyManager.joinLobby(lobbyId, "Player2");
        lobbyManager.startGame(lobbyId);
        assertNotNull(lobbyManager.getGameManager(lobbyId), "Should return GameManager if game started");
    }

    @Test
    void removeLobby_RemovesExistingLobby() {
        String lobbyId = lobbyManager.createLobby();
        assertTrue(lobbyManager.removeLobby(lobbyId), "Should return true when removing existing lobby");
        assertNull(lobbyManager.getLobby(lobbyId), "Lobby should be removed");
    }

    @Test
    void removeLobby_ReturnsFalseIfLobbyDoesNotExist() {
        assertFalse(lobbyManager.removeLobby("nonexistent"), "Should return false for nonexistent lobby");
    }

}
