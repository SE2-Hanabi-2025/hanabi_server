package se2.server.hanabi.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import se2.server.hanabi.api.GameStatus;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.game.HintType;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.services.LobbyManager;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.util.ActionResultType;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GameActionControllerTest {

    @Mock
    private LobbyManager lobbyManager;

    @Mock
    private GameManager gameManager;

    @InjectMocks
    private GameActionController controller;

    private final String LOBBY_ID = "test-lobby";
    private final int PLAYER_ID = 1;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(lobbyManager.getGameManager(LOBBY_ID)).thenReturn(gameManager);
    }

    @Test
    void getGameStatus_Success() {
        // Arrange
        GameStatus mockStatus = mock(GameStatus.class);
        when(gameManager.getStatusFor(PLAYER_ID)).thenReturn(mockStatus);

        // Act
        ResponseEntity<GameStatus> response = controller.getGameStatus(LOBBY_ID, PLAYER_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        verify(gameManager).getStatusFor(PLAYER_ID);
    }

    @Test
    void getGameStatus_LobbyNotFound() {
        // Arrange
        when(lobbyManager.getGameManager(LOBBY_ID)).thenReturn(null);

        // Act
        ResponseEntity<GameStatus> response = controller.getGameStatus(LOBBY_ID, PLAYER_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void playCard_Success() {
        // Arrange
        ActionResult successResult = ActionResult.success("Card played successfully");
        when(gameManager.playCard(PLAYER_ID, 0)).thenReturn(successResult);

        // Act
        ResponseEntity<ActionResult> response = controller.playCard(LOBBY_ID, PLAYER_ID, 0);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successResult, response.getBody());
        verify(gameManager).playCard(PLAYER_ID, 0);
    }

    @Test
    void playCard_InvalidMove() {
        // Arrange
        ActionResult invalidResult = ActionResult.invalid("Not your turn");
        when(gameManager.playCard(PLAYER_ID, 0)).thenReturn(invalidResult);

        // Act
        ResponseEntity<ActionResult> response = controller.playCard(LOBBY_ID, PLAYER_ID, 0);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(invalidResult, response.getBody());
    }
    
    @Test
    void playCard_LobbyNotFound() {
        // Arrange
        when(lobbyManager.getGameManager(LOBBY_ID)).thenReturn(null);

        // Act
        ResponseEntity<ActionResult> response = controller.playCard(LOBBY_ID, PLAYER_ID, 0);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void discardCard_Success() {
        // Arrange
        ActionResult successResult = ActionResult.success("Card discarded successfully");
        when(gameManager.discardCard(PLAYER_ID, 0)).thenReturn(successResult);

        // Act
        ResponseEntity<ActionResult> response = controller.discardCard(LOBBY_ID, PLAYER_ID, 0);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successResult, response.getBody());
        verify(gameManager).discardCard(PLAYER_ID, 0);
    }

    @Test
    void discardCard_InvalidMove() {
        // Arrange
        ActionResult invalidResult = ActionResult.invalid("Cannot discard: hint tokens at maximum");
        when(gameManager.discardCard(PLAYER_ID, 0)).thenReturn(invalidResult);

        // Act
        ResponseEntity<ActionResult> response = controller.discardCard(LOBBY_ID, PLAYER_ID, 0);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(invalidResult, response.getBody());
    }

    @Test
    void discardCard_LobbyNotFound() {
        // Arrange
        when(lobbyManager.getGameManager(LOBBY_ID)).thenReturn(null);

        // Act
        ResponseEntity<ActionResult> response = controller.discardCard(LOBBY_ID, PLAYER_ID, 0);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void giveHint_SuccessForColor() {
        // Arrange
        int toPlayerId = 2;
        ActionResult successResult = ActionResult.success("Hint given successfully");
        when(gameManager.giveHint(PLAYER_ID, toPlayerId, HintType.COLOR, Card.Color.RED))
                .thenReturn(successResult);

        // Act
        ResponseEntity<ActionResult> response = controller.giveHint(
                LOBBY_ID, PLAYER_ID, toPlayerId, HintType.COLOR, "RED");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successResult, response.getBody());
    }

    @Test
    void giveHint_SuccessForNumber() {
        // Arrange
        int toPlayerId = 2;
        ActionResult successResult = ActionResult.success("Hint given successfully");
        when(gameManager.giveHint(eq(PLAYER_ID), eq(toPlayerId), eq(HintType.VALUE), anyInt()))
                .thenReturn(successResult);

        // Act
        ResponseEntity<ActionResult> response = controller.giveHint(
                LOBBY_ID, PLAYER_ID, toPlayerId, HintType.VALUE, "3");

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(successResult, response.getBody());
    }

    @Test
    void giveHint_InvalidColorValue() {
        // Arrange
        int toPlayerId = 2;

        // Act
        ResponseEntity<ActionResult> response = controller.giveHint(
                LOBBY_ID, PLAYER_ID, toPlayerId, HintType.COLOR, "INVALID_COLOR");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ActionResultType.INVALID_MOVE, response.getBody().getType());
    }

    @Test
    void giveHint_InvalidNumberValue() {
        // Arrange
        int toPlayerId = 2;

        // Act
        ResponseEntity<ActionResult> response = controller.giveHint(
                LOBBY_ID, PLAYER_ID, toPlayerId, HintType.VALUE, "10");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ActionResultType.INVALID_MOVE, response.getBody().getType());
    }

    @Test
    void giveHint_NonNumericValue() {
        // Arrange
        int toPlayerId = 2;

        // Act
        ResponseEntity<ActionResult> response = controller.giveHint(
                LOBBY_ID, PLAYER_ID, toPlayerId, HintType.VALUE, "not-a-number");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(ActionResultType.INVALID_MOVE, response.getBody().getType());
    }

    @Test
    void giveHint_InvalidMove() {
        // Arrange
        int toPlayerId = 2;
        ActionResult invalidResult = ActionResult.invalid("Not enough hint tokens");
        when(gameManager.giveHint(eq(PLAYER_ID), eq(toPlayerId), eq(HintType.VALUE), anyInt()))
                .thenReturn(invalidResult);

        // Act
        ResponseEntity<ActionResult> response = controller.giveHint(
                LOBBY_ID, PLAYER_ID, toPlayerId, HintType.VALUE, "3");

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(invalidResult, response.getBody());
    }

    @Test
    void giveHint_LobbyNotFound() {
        // Arrange
        when(lobbyManager.getGameManager(LOBBY_ID)).thenReturn(null);

        // Act
        ResponseEntity<ActionResult> response = controller.giveHint(
                LOBBY_ID, PLAYER_ID, 2, HintType.VALUE, "3");

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void getGameHistory_Success() {
        // Arrange
        List<String> mockHistory = Arrays.asList("Player1 played a card", "Player2 gave a hint");
        when(gameManager.getGameHistory()).thenReturn(mockHistory);

        // Act
        ResponseEntity<List<String>> response = controller.getGameHistory(LOBBY_ID);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(mockHistory, response.getBody());
        verify(gameManager).getGameHistory();
    }

    @Test
    void getGameHistory_LobbyNotFound() {
        // Arrange
        when(lobbyManager.getGameManager(LOBBY_ID)).thenReturn(null);

        // Act
        ResponseEntity<List<String>> response = controller.getGameHistory(LOBBY_ID);

        // Assert
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}