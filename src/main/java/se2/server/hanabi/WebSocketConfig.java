package se2.server.hanabi;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import se2.server.hanabi.services.LobbyManager;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    private final LobbyManager lobbyManager;
    
    @Autowired
    public WebSocketConfig(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }
    
    @Bean
    public SimpleWebSocketHandler webSocketHandler() {
        return new SimpleWebSocketHandler(lobbyManager);
    }
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // Register main game action handler
        registry.addHandler(webSocketHandler(), "/ws/game").setAllowedOrigins("*");
    }
}
