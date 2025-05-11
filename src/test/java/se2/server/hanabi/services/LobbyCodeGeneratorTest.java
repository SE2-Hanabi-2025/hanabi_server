package se2.server.hanabi.services;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LobbyCodeGeneratorTest {

    @Test
    void testGenerateLobbyCode() {
        String code = LobbyCodeGenerator.generateLobbyCode();
        assertNotNull(code, "Generated code should not be null");
        assertEquals(6, code.length(), "Generated code should have a length of 6");
        assertTrue(code.matches("[ABCDEFGHJKLMNPQRSTUVWXYZ123456789]+"), "Generated code should match the allowed character set");
    }

    @Test
    void testGenerateLobbyCodeRandomness() {
        String code1 = LobbyCodeGenerator.generateLobbyCode();
        String code2 = LobbyCodeGenerator.generateLobbyCode();
        assertNotEquals(code1, code2, "Generated codes should be random and unique");
    }
}
