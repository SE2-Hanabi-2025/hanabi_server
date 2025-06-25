package se2.server.hanabi.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.model.Lobby;

import static org.junit.jupiter.api.Assertions.*;

 public class LobbyManagerTest {

    private LobbyManager lobbyManager;

    private static final int Red = 2131230890;
    private static final int Blue = 2131230891;
    private static final int White = 0; // default avatar
    
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
        @SuppressWarnings("unused")
        Lobby lobby = lobbyManager.getLobby(lobbyId);

        for (int i = 0; i < 5; i++) {
            lobbyManager.joinLobby(lobbyId, "Player" + i, Red + i);
        }

        int result = lobbyManager.joinLobby(lobbyId, "Player6", White);

        assertEquals(result, -1, "Lobby should not allow more than 5 players");
    }

    @Test
    void joinLobby_WhenGameStarted() {
        String lobbyId = lobbyManager.createLobby();
        
        // Add the minimum required players to start a game (2 players)
        lobbyManager.joinLobby(lobbyId, "Player1",Red);
        lobbyManager.joinLobby(lobbyId, "Player2", Blue);
        
        // Start the game properly through the lobbyManager
        boolean gameStarted = lobbyManager.startGame(lobbyId);
        assertTrue(gameStarted, "Game should start successfully with 2 players");

        // Now try to join after game is started
        int result = lobbyManager.joinLobby(lobbyId, "NewPlayer", White);

        assertEquals(result, -1,"No player should be able to join once the game has started");
    }

    @Test
    void joinLobby_SuccessfulJoin() {
        String lobbyId = lobbyManager.createLobby();
        int result = lobbyManager.joinLobby(lobbyId, "Player1", Red);

        assertNotEquals(result, -1,"Player should be able to join the lobby");
    }

    @Test
    void joinLobby_AvatarPersist(){
        String lobbyId = lobbyManager.createLobby();

        lobbyManager.joinLobby(lobbyId, "Player1", Red);
        lobbyManager.joinLobby(lobbyId, "Player2", Blue);

        Lobby lobby = lobbyManager.getLobby(lobbyId);

        assertEquals(Red, lobby.getPlayers().get(0).getAvatarResID(), "Player1 should have red avatar");
        assertEquals(Blue, lobby.getPlayers().get(1).getAvatarResID(), "Player2 should have blue avatar");
    }

    @Test
    void getAllLobbies_ReturnsLobbies() {
        @SuppressWarnings("unused")
        String lobbyId1 = lobbyManager.createLobby();
        @SuppressWarnings("unused")
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
        lobbyManager.joinLobby(lobbyId, "Player1", Red);
        lobbyManager.joinLobby(lobbyId, "Player2", Blue);
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
        lobbyManager.joinLobby(lobbyId, "Player1", Red);
        lobbyManager.joinLobby(lobbyId, "Player2", Blue);
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

    @Test
     void leaveLobby_RemovesPlayer(){
        String lobbyId = lobbyManager.createLobby();
        int player1 = lobbyManager.joinLobby(lobbyId, "Player1", Red);
        int player2 = lobbyManager.joinLobby(lobbyId, "Player 2", Blue);

        assertEquals(2, lobbyManager.getLobby(lobbyId).getPlayers().size(), "2 players");

                boolean removed = lobbyManager.leaveLobby(lobbyId, player1);
        assertTrue(removed, "Player 1 is removed");
        assertEquals(1, lobbyManager.getLobby(lobbyId).getPlayers().size(), "1 player left");
        assertEquals(player2, lobbyManager.getLobby(lobbyId).getPlayers().get(0).getId());

    }

    @Test
     void leaveLobby_ReturnNoLobby(){
        boolean removed = lobbyManager.leaveLobby("wrongLobby", 123);
        assertFalse(removed, "Return false if lobby doesnt exist");
    }

    @Test
     void leaveLobby_ReturnNoPlayer(){
        String lobbyId = lobbyManager.createLobby();
        lobbyManager.joinLobby(lobbyId, "Player 1", Red);
        boolean removed = lobbyManager.leaveLobby(lobbyId, 12345);
        assertFalse(removed, "Return false if player doesnt exist");
    }

}
