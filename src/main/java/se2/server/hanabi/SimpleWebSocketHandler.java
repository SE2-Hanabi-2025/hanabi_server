package se2.server.hanabi;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se2.server.hanabi.game.GameLogger;

public class SimpleWebSocketHandler extends TextWebSocketHandler {
    private static final GameLogger logger = new GameLogger();

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received from client: " + payload);
        session.sendMessage(new TextMessage("Hello back from server!"));
    }
}
