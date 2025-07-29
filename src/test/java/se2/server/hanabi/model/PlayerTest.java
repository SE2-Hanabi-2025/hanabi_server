package se2.server.hanabi.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void testPlayerConstructorWithName() {
        Player player = new Player("Vlado");
        assertEquals("Vlado", player.getName());
        assertTrue(player.getId() >= 0);
    }

    @Test
    void testSetName() {
        Player player = new Player("Bob");
        player.setName("Charlie");
        assertEquals("Charlie", player.getName());
    }

    @Test
    void testGetId() {
        Player player = new Player("Dave");
        assertTrue(player.getId() >= 0);
    }

    @Test
    void testUniqueIds() {
        Player player1 = new Player("Eve");
        Player player2 = new Player("Frank");
        assertNotEquals(player1.getId(), player2.getId());
    }

    @Test
    void testSetNameToNull() {
        Player player = new Player("Grace");
        player.setName(null);
        assertNull(player.getName());
    }

    @Test
    void testSetNameToEmptyString() {
        Player player = new Player("Hank");
        player.setName("");
        assertEquals("", player.getName());
    }

    @Test
    void testSetNameToWhitespace() {
        Player player = new Player("Grace");
        player.setName("   ");
        assertEquals("   ", player.getName());
    }

    @Test
    void testConstructorWithEmptyName() {
        Player player = new Player("");
        assertEquals("", player.getName());
        assertTrue(player.getId() >= 0);
    }

    @Test
    void testConstructorWithNullName() {
        Player player = new Player((String) null);
        assertNull(player.getName());
        assertTrue(player.getId() >= 0);
    }

    @Test
    void testIdIncrement() {
        Player player1 = new Player("Player1");
        Player player2 = new Player("Player2");
        assertEquals(player1.getId() + 1, player2.getId());
    }
}