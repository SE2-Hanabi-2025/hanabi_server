package se2.server.hanabi.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ActionResultTest {

    @Test
    void testSuccessResult() {
        ActionResult result = ActionResult.success("Success message");
        assertTrue(result.isSuccess());
        assertEquals("Success message", result.getMessage());
        assertEquals(ActionResultType.SUCCESS, result.getType());
    }

    @Test
    void testFailureResult() {
        ActionResult result = ActionResult.failure("Failure message");
        assertFalse(result.isSuccess());
        assertEquals("Failure message", result.getMessage());
        assertEquals(ActionResultType.FAILURE, result.getType());
    }

    @Test
    void testInvalidResult() {
        ActionResult result = ActionResult.invalid("Invalid move");
        assertFalse(result.isSuccess());
        assertEquals("Invalid move", result.getMessage());
        assertEquals(ActionResultType.INVALID_MOVE, result.getType());
    }
}
