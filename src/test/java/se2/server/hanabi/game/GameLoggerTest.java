package se2.server.hanabi.game;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    void testWarnLogging() {
        logger.warn("This is a warning message.");
        List<String> history = logger.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("[WARN] This is a warning message."));
    }
    
    @Test
    void testErrorLogging() {
        logger.error("This is an error message.");
        List<String> history = logger.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("[ERROR] This is an error message."));
    }

    @Test
    void testClearHistory() {
        logger.info("First message");
        logger.warn("Second message");
        logger.clear();
        assertTrue(logger.getHistory().isEmpty());
    }

    @Test
    void testGetHistory() {
        logger.info("Message 1");
        logger.warn("Message 2");
        List<String> history = logger.getHistory();
        assertEquals(2, history.size());
        assertTrue(history.contains("[INFO] Message 1"));
        assertTrue(history.contains("[WARN] Message 2"));
    }

    @Test
    void testLogEmptyMessage() {
        logger.info("");
        List<String> history = logger.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("[INFO] "));
    }

    @Test
    void testLogNullMessage() {
        logger.info(null);
        List<String> history = logger.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains("[INFO] null"));
    }

    @Test
    void testLogVeryLongMessage() {
        String longMessage = "a".repeat(10000);
        logger.info(longMessage);
        List<String> history = logger.getHistory();
        assertEquals(1, history.size());
        assertTrue(history.get(0).contains(longMessage));
    }

    @Test
    void testLogOrder() {
        logger.info("First message");
        logger.warn("Second message");
        logger.error("Third message");
        List<String> history = logger.getHistory();
        assertEquals(3, history.size());
        assertTrue(history.get(0).contains("[INFO] First message"));
        assertTrue(history.get(1).contains("[WARN] Second message"));
        assertTrue(history.get(2).contains("[ERROR] Third message"));
    }

    @Test
    void testHistoryImmutability() {
        logger.info("Immutable test");
        List<String> history = logger.getHistory();
        assertThrows(UnsupportedOperationException.class, () -> history.add("New entry"));
    }

}
