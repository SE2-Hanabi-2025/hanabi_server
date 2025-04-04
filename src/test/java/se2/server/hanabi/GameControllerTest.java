package se2.server.hanabi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@WebMvcTest(GameController.class)
public class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testConnectEndpoint() throws Exception {
        mockMvc.perform(get("/connect"))
            .andExpect(status().isOk())
            .andExpect(content().string("Connection established with the server!"));
    }

    @Test
    void testStartGameEndpoint() throws Exception {
        mockMvc.perform(get("/game/start"))
            .andExpect(status().isOk())
            .andExpect(content().string("Game started! New deck created."));
    }

    @Test
    void testDrawCardEndpoint() throws Exception {
        mockMvc.perform(get("/game/start"));
    
        mockMvc.perform(get("/game/draw"))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Drew a card:")));
    }
    

    @Test
    void testDrawCardEmptyDeck() throws Exception {
        for (int i = 0; i < 50; i++) {
            mockMvc.perform(get("/game/draw"));
        }

        mockMvc.perform(get("/game/draw"))
            .andExpect(status().isOk())
            .andExpect(content().string("No more cards in the deck!"));
    }

}
