package se2.server.hanabi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import se2.server.hanabi.controllers.LobbyController;
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

    @Test
    void createLobby_ReturnLobbyId() throws Exception {

        when(lobbyManager.createLobby()).thenReturn("mockedLobby123");

        mockMvc.perform(get("/create-lobby"))
                .andExpect(status().isOk())
                .andExpect(content().string("mockedLobby123"));
    }

    @Test
    void joinLobby_successful() throws Exception {
        Lobby mockLobby = new Lobby();
        when(lobbyManager.getLobby("test123")).thenReturn(mockLobby);
        when(lobbyManager.joinLobby("test123", "Alice")).thenReturn(true);

        mockMvc.perform(get("/join-lobby/test123").param("name", "Alice"))
                .andExpect(status().isOk())
                .andExpect(content().string("Joined lobby: test123"));
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
        Lobby startedLobby = new Lobby();
        startedLobby.startGame(); // setzt isGameStarted auf true
        when(lobbyManager.getLobby("started")).thenReturn(startedLobby);

        mockMvc.perform(get("/join-lobby/started").param("name", "Test"))
                .andExpect(status().isConflict())
                .andExpect(content().string("Game already started."));
    }

    @Test
    void joinLobby_lobbyFull() throws Exception {
        Lobby fullLobby = new Lobby();
        List<Player> fullList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            fullList.add(new se2.server.hanabi.model.Player("Player" + i));
        }

        fullLobby.getPlayers().addAll(fullList);
        when(lobbyManager.getLobby("fullLobby")).thenReturn(fullLobby);

        mockMvc.perform(get("/join-lobby/fullLobby").param("name", "Extra"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Lobby is full."));
    }

    @Test
    void joinLobby_unknownJoinError() throws Exception {
        Lobby lobby = new Lobby();
        when(lobbyManager.getLobby("errorLobby")).thenReturn(lobby);
        when(lobbyManager.joinLobby("errorLobby", "Buggy")).thenReturn(false);

        mockMvc.perform(get("/join-lobby/errorLobby").param("name", "Buggy"))
                .andExpect(status().isInternalServerError())
                .andExpect(content().string("Unknown error while joining lobby."));
    }

    @TestConfiguration
    static class MockConfig {

        @Bean
        @Primary
        public LobbyManager lobbyManager() {
            return mock(LobbyManager.class);
        }
    }
}