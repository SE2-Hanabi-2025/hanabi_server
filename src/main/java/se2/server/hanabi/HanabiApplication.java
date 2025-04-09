package se2.server.hanabi;

import com.jcraft.jsch.ECDH;
import com.jcraft.jsch.Session;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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
