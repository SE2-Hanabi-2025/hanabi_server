package se2.server.hanabi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import se2.server.hanabi.game.GameLogger;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.GameActionMessage;
import se2.server.hanabi.services.LobbyManager;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.game.HintType;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleWebSocketHandler extends TextWebSocketHandler {
    private static final GameLogger logger = new GameLogger();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final LobbyManager lobbyManager;
    
    // Map to store sessions by player ID and lobby ID
    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();

    public SimpleWebSocketHandler(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // Session parameters should include lobbyId and playerId

        URI uri = session.getUri();
        if (uri == null) {
            logger.error("WebSocket connection (Session ID: {}) rejected: Session URI is null." + session.getId());
            session.close(CloseStatus.SERVER_ERROR.withReason("Internal server error: Missing URI"));
            return;
        }

        String query = uri.getQuery();
        if (query == null || query.trim().isEmpty()) {
            logger.error("WebSocket connection (Session ID: {}) rejected: Missing query parameters in URI."+ session.getId());
            session.close(CloseStatus.BAD_DATA.withReason("lobbyId and playerId are required query parameters."));
            return;
        }

        Map<String, String> parameters = extractParameters(query);
        String lobbyId = parameters.get("lobbyId");
        String playerId = parameters.get("playerId");
        
        if (lobbyId != null && playerId != null) {
            String sessionKey = createSessionKey(lobbyId, playerId);
            sessions.put(sessionKey, session);
            logger.info("WebSocket connection established for player " + playerId + " in lobby " + lobbyId);
        } else {
            logger.error("WebSocket connection rejected: missing lobbyId or playerId");
            session.close(CloseStatus.BAD_DATA);
        }
    
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        logger.info("Received from client: " + payload);
        
        try {
            GameActionMessage actionMessage = objectMapper.readValue(payload, GameActionMessage.class);
            processGameAction(session, actionMessage);
        } catch (IOException e) {
            logger.error("Error processing message: " + e.getMessage());
            session.sendMessage(new TextMessage("{\"error\": \"Invalid message format\"}"));
        }
    }
    
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws IOException {
        // Remove session from our map
        sessions.values().removeIf(s -> s.getId().equals(session.getId()));
        logger.info("WebSocket connection closed with status: " + status);
    }
      
    private void processGameAction(WebSocketSession session, GameActionMessage actionMessage) throws IOException  {
        String lobbyId = actionMessage.getLobbyId();
        GameManager gameManager = lobbyManager.getGameManager(lobbyId);
        
        if (gameManager == null) {
            session.sendMessage(new TextMessage("{\"error\": \"Game or lobby not found\"}"));
            return;
        }
        
        // Check if the actionType is null
        if (actionMessage.getActionType() == null) {
            session.sendMessage(new TextMessage("{\"error\": \"Missing or invalid action type. Make sure to include 'action' field.\"}"));
            return;
        }
        
        ActionResult result;
        
        switch (actionMessage.getActionType()) {
            case PLAY:
                result = gameManager.playCard(actionMessage.getPlayerId(), actionMessage.getCardIndex());
                break;
            case DISCARD:
                result = gameManager.discardCard(actionMessage.getPlayerId(), actionMessage.getCardIndex());
                break;
            case HINT:
                // Convert hint value based on hint type
                Object value;
                if (actionMessage.getHintType() == HintType.COLOR) {
                    try {
                        value = Card.Color.valueOf(actionMessage.getHintValue().toUpperCase());
                    } catch (IllegalArgumentException e) {
                        session.sendMessage(new TextMessage("{\"error\": \"Invalid color value\"}"));
                        return;
                    }
                } else {
                    try {
                        value = Integer.parseInt(actionMessage.getHintValue());
                        if ((Integer)value < 1 || (Integer)value > 5) {
                            session.sendMessage(new TextMessage("{\"error\": \"Invalid card value (must be 1-5)\"}"));
                            return;
                        }
                    } catch (NumberFormatException e) {
                        session.sendMessage(new TextMessage("{\"error\": \"Invalid hint value format\"}"));
                        return;
                    }
                }
                
                result = gameManager.giveHint(
                    actionMessage.getPlayerId(), 
                    actionMessage.getToPlayerId(), 
                    actionMessage.getHintType(), 
                    value
                );
                break;
            case DEFUSE:
                result = gameManager.defuseStrike(actionMessage.getPlayerId());
                break;
            default:
                session.sendMessage(new TextMessage("{\"error\": \"Unknown action type\"}"));
                return;
        }
        
        // Send result back to the client who made the action
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(result)));
        
        // Update game state for all players in the lobby
        broadcastGameUpdate(lobbyId, gameManager);
    }
    
    private void broadcastGameUpdate(String lobbyId, GameManager gameManager) {
        for (Map.Entry<String, WebSocketSession> entry : sessions.entrySet()) {
            if (entry.getKey().startsWith(lobbyId + ":") && entry.getValue().isOpen()) {
                try {
                    int playerId = Integer.parseInt(entry.getKey().split(":")[1]);
                    String gameStatus = objectMapper.writeValueAsString(gameManager.getStatusFor(playerId));
                    entry.getValue().sendMessage(new TextMessage(gameStatus));
                } catch (Exception e) {
                    logger.error("Error broadcasting game update: " + e.getMessage());
                }
            }
        }
    }
    
    private Map<String, String> extractParameters(String query) {
        Map<String, String> parameters = new ConcurrentHashMap<>();
        if (query == null) return parameters;
        
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        
        return parameters;
    }
    
    private String createSessionKey(String lobbyId, String playerId) {
        return lobbyId + ":" + playerId;
    }
}
