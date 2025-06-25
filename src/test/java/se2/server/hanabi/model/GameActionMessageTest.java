package se2.server.hanabi.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GameActionMessageTest {
    @Test
    void testDefuseAttemptFields() {
        GameActionMessage msg = new GameActionMessage();
        msg.setType("DEFUSE_ATTEMPT");
        msg.setActionType(GameActionMessage.ActionType.DEFUSE_ATTEMPT);
        msg.setSequence(Arrays.asList("DOWN", "DOWN", "UP", "DOWN"));
        msg.setProximity("DARK");
        assertEquals("DEFUSE_ATTEMPT", msg.getType());
        assertEquals(GameActionMessage.ActionType.DEFUSE_ATTEMPT, msg.getActionType());
        assertEquals(Arrays.asList("DOWN", "DOWN", "UP", "DOWN"), msg.getSequence());
        assertEquals("DARK", msg.getProximity());
    }

    @Test
    void testJsonSerialization() throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        GameActionMessage msg = new GameActionMessage();
        msg.setType("DEFUSE_ATTEMPT");
        msg.setActionType(GameActionMessage.ActionType.DEFUSE_ATTEMPT);
        msg.setSequence(Arrays.asList("DOWN", "DOWN", "UP", "DOWN"));
        msg.setProximity("DARK");
        String json = mapper.writeValueAsString(msg);
        GameActionMessage deserialized = mapper.readValue(json, GameActionMessage.class);
        assertEquals(msg.getType(), deserialized.getType());
        assertEquals(msg.getActionType(), deserialized.getActionType());
        assertEquals(msg.getSequence(), deserialized.getSequence());
        assertEquals(msg.getProximity(), deserialized.getProximity());
    }
}
