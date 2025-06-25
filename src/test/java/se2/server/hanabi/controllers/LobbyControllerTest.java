package se2.server.hanabi.controllers;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import se2.server.hanabi.model.Lobby;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.services.LobbyManager;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.List;


import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LobbyController.class)
public class LobbyControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LobbyManager lobbyManager;

    private static final int RED = 2131230890;
    private static final int BLUE = 2131230891;
    private static final int WHITE = 0;

    @Test
    void createLobby_ReturnLobbyId() throws Exception {

        when(lobbyManager.createLobby()).thenReturn("mockedLobby123");

        mockMvc.perform(get("/create-lobby"))
                .andExpect(status().isOk())
                .andExpect(content().string("mockedLobby123"));
    }

    @Test
    void joinLobby_successful() throws Exception {
        Lobby mockLobby = new Lobby("test123");
        when(lobbyManager.getLobby("test123")).thenReturn(mockLobby);
        when(lobbyManager.joinLobby("test123", "Alice", WHITE)).thenReturn(15);

        mockMvc.perform(get("/join-lobby/test123").param("name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(content().string("Joined lobby: test123 PlayerID: 15"));
    }

    @Test
    void joinLobby_lobbyNotFound() throws Exception {
        when(lobbyManager.getLobby("unknown")).thenReturn(null);

        mockMvc.perform(get("/join-lobby/unknown").param("name", "Any"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Lobby not found."));
    }

    @Test
    void joinLobby_gameAlreadyStarted() throws Exception {
        Lobby startedLobby = new Lobby("started");

        startedLobby.getPlayers().add(new Player("Player1", RED));
        startedLobby.getPlayers().add(new Player("Player2", BLUE));
        startedLobby.startGame();
        when(lobbyManager.getLobby("started")).thenReturn(startedLobby);

        mockMvc.perform(get("/join-lobby/started").param("name", "Test"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Game already started."));
    }

    @Test
    void joinLobby_lobbyFull() throws Exception {
        Lobby fullLobby = new Lobby("fulllobby");
        List<Player> fullList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fullList.add(new se2.server.hanabi.model.Player("Player" + i, RED));
        }

        fullLobby.getPlayers().addAll(fullList);
        when(lobbyManager.getLobby("fullLobby")).thenReturn(fullLobby);

        mockMvc.perform(get("/join-lobby/fullLobby").param("name", "Extra"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lobby is full."));
    }

    @Test
    void joinLobby_unknownJoinError() throws Exception {
        Lobby lobby = new Lobby("error");
        when(lobbyManager.getLobby("errorLobby")).thenReturn(lobby);
        when(lobbyManager.joinLobby("errorLobby", "Buggy", WHITE)).thenReturn(-1);

        mockMvc.perform(get("/join-lobby/errorLobby").param("name", "Buggy"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unknown error while joining lobby."));
    }

    @Test
    void startGame_successful() throws Exception {
        Lobby lobby = new Lobby("game123");
        lobby.getPlayers().add(new Player("P1", RED));
        lobby.getPlayers().add(new Player("P2", BLUE));
        when(lobbyManager.getLobby("game123")).thenReturn(lobby);
        when(lobbyManager.startGame("game123", true)).thenReturn(true);

        mockMvc.perform(get("/start-game/game123").param("isCasualMode", "true"))
                .andExpect(status().isOk())
                .andExpect(content().string("Game started successfully"));
    }

    @Test
    void startGame_lobbyNotFound() throws Exception {
        when(lobbyManager.getLobby("notfound")).thenReturn(null);

        mockMvc.perform(get("/start-game/notfound").param("isCasualMode", "true"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Lobby not found"));
    }

    @Test
    void startGame_gameAlreadyStarted() throws Exception {
        Lobby lobby = new Lobby("alreadyStarted");
        lobby.getPlayers().add(new Player("P1", RED));
        lobby.getPlayers().add(new Player("P2", BLUE));
        lobby.startGame();
        when(lobbyManager.getLobby("alreadyStarted")).thenReturn(lobby);

        mockMvc.perform(get("/start-game/alreadyStarted").param("isCasualMode", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Game already started"));
    }

    @Test
    void startGame_notEnoughPlayers() throws Exception {
        Lobby lobby = new Lobby("fewPlayers");
        lobby.getPlayers().add(new Player("P1", RED));
        when(lobbyManager.getLobby("fewPlayers")).thenReturn(lobby);

        mockMvc.perform(get("/start-game/fewPlayers").param("isCasualMode", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Not enough players to start game (minimum 2)"));
    }

    @Test
    void startGame_unknownError() throws Exception {
        Lobby lobby = new Lobby("unknownError");
        lobby.getPlayers().add(new Player("P1", RED));
        lobby.getPlayers().add(new Player("P2", BLUE));
        when(lobbyManager.getLobby("unknownError")).thenReturn(lobby);
        when(lobbyManager.startGame("unknownError")).thenReturn(false);

        mockMvc.perform(get("/start-game/unknownError").param("isCasualMode", "false"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Cannot start game: Unknown error"));
    }

    @Test
    void getAllLobbies_returnsLobbies() throws Exception {
        List<Lobby> lobbies = new ArrayList<>();
        lobbies.add(new Lobby("lobby1"));
        lobbies.add(new Lobby("lobby2"));
        when(lobbyManager.getAllLobbies()).thenReturn(lobbies);

        mockMvc.perform(get("/lobbies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("lobby1"))
                .andExpect(jsonPath("$[1].id").value("lobby2"));
    }


    @Test
    void testGetPlayersInLobby() throws Exception {
        String lobbyId = "testLobby";

        Player player1 = new Player("Player1", RED);
        Player player2 = new Player("Player2", BLUE);

        Lobby mockLobby = mock(Lobby.class);
        when(mockLobby.getPlayers()).thenReturn(List.of(player1, player2));
        when(lobbyManager.getLobby(lobbyId)).thenReturn(mockLobby);

        mockMvc.perform(get("/lobby/" + lobbyId + "/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Player1"))
                .andExpect(jsonPath("$[0].avatarResID").value(RED))
                .andExpect(jsonPath("$[1].name").value("Player2"))
                .andExpect(jsonPath("$[1].avatarResID").value(BLUE));
    }

    @Test
    void testGetPlayersInLobby_DefaultAvatar() throws Exception {
        String lobbyId = "testLobby";

        Player player1 = new Player("Player1", WHITE);
        Player player2 = new Player("Player2", RED);

        Lobby mockLobby = mock(Lobby.class);
        when(mockLobby.getPlayers()).thenReturn(List.of(player1, player2));
        when(lobbyManager.getLobby(lobbyId)).thenReturn(mockLobby);

        mockMvc.perform(get("/lobby/" + lobbyId + "/players"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Player1"))
                .andExpect(jsonPath("$[0].avatarResID").value(WHITE))
                .andExpect(jsonPath("$[1].name").value("Player2"))
                .andExpect(jsonPath("$[1].avatarResID").value(RED));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        @Primary
        public LobbyManager lobbyManager() {
            return mock(LobbyManager.class);
        }
    }

    @Test
    void isGameStarted_shouldReturnTrueIfStarted() throws Exception {
        String lobbyId = "test123";
        Lobby mockLobby = Mockito.mock(Lobby.class);
        when(lobbyManager.getLobby(lobbyId)).thenReturn(mockLobby);
        when(mockLobby.isGameStarted()).thenReturn(true);

        mockMvc.perform(get("/start-game/{id}/status", lobbyId))
                .andExpect(status().isOk())
                .andExpect(content().string("true"));
    }

    @Test
    void isGameStarted_shouldReturnFalseIfNotStarted() throws Exception {
        String lobbyId = "test123";
        Lobby mockLobby = Mockito.mock(Lobby.class);
        when(lobbyManager.getLobby(lobbyId)).thenReturn(mockLobby);
        when(mockLobby.isGameStarted()).thenReturn(false);

        mockMvc.perform(get("/start-game/{id}/status", lobbyId))
                .andExpect(status().isOk())
                .andExpect(content().string("false"));
    }

    @Test
    void isGameStarted_shouldReturnNotFoundIfLobbyMissing() throws Exception {
        when(lobbyManager.getLobby("invalid")).thenReturn(null);

        mockMvc.perform(get("/start-game/{id}/status", "invalid"))
                .andExpect(status().isNotFound());
    }

    @Test
    void leaveLobby_successful() throws Exception {
        when(lobbyManager.leaveLobby("lobbyId", 1)).thenReturn(true);

        mockMvc.perform(get("/leave-lobby/lobbyId/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Player1left lobby successfully"));
    }

    @Test
    void leaveLobby_failed()throws Exception {
        when(lobbyManager.leaveLobby("wrongLobby",1)).thenReturn(false);

        mockMvc.perform(get("/leave-lobby/wrongLobby/1"))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Failed to leave lobby"));
    }

}