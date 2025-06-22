package se2.server.hanabi;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@OpenAPIDefinition(
		info = @Info(
				title = "Hanabi Game API",
				version = "1.0",
				description = "API for managing Hanabi Board Game Backend Logic."
		)
)
public class HanabiApplication {


	public static void main(String[] args) {
		SpringApplication.run(HanabiApplication.class, args);
	}


}
