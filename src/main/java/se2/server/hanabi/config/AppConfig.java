package se2.server.hanabi.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se2.server.hanabi.services.LobbyManager;

@Configuration
public class AppConfig {

    @Bean
    public LobbyManager lobbyManager() {
        return new LobbyManager();
    }
}
