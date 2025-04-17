package se2.server.hanabi;

import com.jcraft.jsch.ECDH;
import com.jcraft.jsch.Session;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
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

	/*
	@Autowired
	private SSHConnector sshConnector;


	 */
	public static void main(String[] args) {
		SpringApplication.run(HanabiApplication.class, args);
	}

	/*
	@PostConstruct
	public void init() {
		try {
			Session session =sshConnector.connect();
			System.out.println("SSH-Verbindung erfolgreich!");
			session.disconnect();
		} catch (Exception e) {
			System.out.println("Verbindung zum SSH-Server fehlgeschlagen");
		}
	}

	 */
}
