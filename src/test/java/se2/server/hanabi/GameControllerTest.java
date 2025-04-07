package se2.server.hanabi;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

@SpringBootTest
@AutoConfigureMockMvc
class GameControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void testConnect() throws Exception {
        mockMvc.perform(get("/connect"))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection established with the server!"));
    }

    @Test
    void testStartGame() throws Exception {
        mockMvc.perform(get("/game/start"))
                .andExpect(status().isOk())
                .andExpect(content().string("Game started! New deck created."));
    }

    @Test
    void testDrawCard() throws Exception {
        // Start the game to initialize the deck
        mockMvc.perform(get("/game/start")).andExpect(status().isOk());

        // Now, draw a card
        mockMvc.perform(get("/game/draw"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("Drew a card: ")));
    }

    @Test
    void testDrawCardWhenDeckIsEmpty() throws Exception {
        // Start the game and draw all cards from the deck
        mockMvc.perform(get("/game/start")).andExpect(status().isOk());

        // Draw all cards from the deck until it's empty
        for (int i = 0; i < 50; i++) { // We know there are 25 cards in total
            mockMvc.perform(get("/game/draw")).andExpect(status().isOk());
        }

        // Now, try to draw from an empty deck
        mockMvc.perform(get("/game/draw"))
                .andExpect(status().isOk())
                .andExpect(content().string("No more cards in the deck!"));
    }
}
