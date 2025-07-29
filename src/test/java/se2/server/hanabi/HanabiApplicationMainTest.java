package se2.server.hanabi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class HanabiApplicationTests {

	@Test
	void contextLoads() {
		// This test method intentionally left empty
		// It verifies that the Spring application context loads successfully
		// The test will fail if the application context cannot be created
		// which can happen due to misconfiguration, bean creation errors, etc.
	}

	@Test
    void testMainMethodRuns() {
        assertDoesNotThrow(() -> HanabiApplication.main(new String[]{}));
    }

}
