package se2.server.hanabi.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the GameLogger class.
 */
class GameLoggerTest {

    private GameLogger logger;

    @BeforeEach
    void setUp() {
        logger = new GameLogger();
    }

    @Test
    void testInfoLogging() {
        logger.info("This is an info message.");
        List<String> history = logger.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("[INFO] This is an info message."));
    }

    
}
