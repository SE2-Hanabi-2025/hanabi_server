package se2.server.hanabi.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testPlayerConstructorWithName() {
        Player player = new Player("Alice");
        assertEquals("Alice", player.getName());
        assertTrue(player.getId() >= 0); // Ensure ID is non-negative
    }

}