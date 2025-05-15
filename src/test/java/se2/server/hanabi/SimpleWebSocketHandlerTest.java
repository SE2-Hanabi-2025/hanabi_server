package se2.server.hanabi;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import org.mockito.Mockito;

import static org.mockito.Mockito.*;

class SimpleWebSocketHandlerTest {
    private SimpleWebSocketHandler handler;
    private WebSocketSession session;

    @BeforeEach
    void setUp() {
        handler = new SimpleWebSocketHandler();
        session = Mockito.mock(WebSocketSession.class);
    }

    @Test
    void testHandleTextMessage_logsAndResponds() throws Exception {
        String clientMessage = "Test message from client";
        TextMessage message = new TextMessage(clientMessage);

        handler.handleTextMessage(session, message);
        
        verify(session, times(1)).sendMessage(new TextMessage("Hello back from server!"));
    }
}
