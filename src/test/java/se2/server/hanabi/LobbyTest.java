package se2.server.hanabi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se2.server.hanabi.model.Lobby;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.services.LobbyCodeGenerator;

import static org.junit.jupiter.api.Assertions.*;


public class LobbyTest {
    private Lobby lobby;

    @BeforeEach
    public void setUp() {
        String code = LobbyCodeGenerator.generateLobbyCode();
        lobby = new Lobby(code);
    }

    @Test
    public void testStartGame() {
        assertFalse(lobby.isGameStarted(), "Game should not be started");
        
        // Add two players to the lobby so we can start the game
        lobby.getPlayers().add(new Player("Player1"));
        lobby.getPlayers().add(new Player("Player2"));
        
        boolean result = lobby.startGame();
        assertTrue(result, "Game should successfully start");
        assertTrue(lobby.isGameStarted(), "Game should be started");
    }

    @Test
    public void testGetId_() {
        String id = lobby.getId();
        assertNotNull(id, "Lobby-ID darf nicht null sein");
        assertFalse(id.trim().isEmpty(), "Lobby-ID darf nicht leer sein");
        System.out.println(id.trim());
    }

}
