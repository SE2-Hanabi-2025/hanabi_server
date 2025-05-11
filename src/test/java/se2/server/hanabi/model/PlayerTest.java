package se2.server.hanabi.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testPlayerConstructorWithName() {
        Player player = new Player("Vlado");
        assertEquals("Vlado", player.getName());
        assertTrue(player.getId() >= 0); // Ensure ID is non-negative
    }

    @Test
    void testPlayerConstructorWithId() {
        Player player = new Player(42);
        assertEquals(42, player.getId());
        assertEquals("Player42", player.getName()); // Default name based on ID
    }

    @Test
    void testSetName() {
        Player player = new Player("Bob");
        player.setName("Charlie");
        assertEquals("Charlie", player.getName());
    }

}