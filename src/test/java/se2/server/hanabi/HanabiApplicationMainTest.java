package se2.server.hanabi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class HanabiApplicationTests {

	@Test
	void contextLoads() {
	}

	@Test
    void testMainMethodRuns() {
        assertDoesNotThrow(() -> HanabiApplication.main(new String[]{}));
    }

}
