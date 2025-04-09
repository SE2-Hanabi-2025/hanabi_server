package se2.server.hanabi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.model.Lobby;

import static org.junit.jupiter.api.Assertions.*;


public class LobbyTest {
    private Lobby lobby;

    @BeforeEach
    public void setUp() {
        lobby = new Lobby();
    }

    @Test
    public void testStartGame() {
        assertFalse(lobby.isGameStarted(), "Game should not be started");
        lobby.startGame();
        assertTrue(lobby.isGameStarted(), "Game should be started");
    }

    @Test
    public void testGetId_() {
        String id = lobby.getId();
        assertNotNull(id, "Lobby-ID darf nicht null sein");
        assertFalse(id.trim().isEmpty(), "Lobby-ID darf nicht leer sein");
    }

}
