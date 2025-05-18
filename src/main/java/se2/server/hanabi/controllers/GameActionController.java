package se2.server.hanabi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import se2.server.hanabi.api.GameStatus;
import se2.server.hanabi.util.ActionResult;
import se2.server.hanabi.game.GameManager;
import se2.server.hanabi.game.HintType;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.services.LobbyManager;
import se2.server.hanabi.util.ActionResultType;

import java.util.List;

@RestController
@Tag(name = "Game Actions API", description = "Endpoints to perform game actions like playing cards, discarding, and giving hints")
@RequestMapping("/api/game")
public class GameActionController {
    
    private final LobbyManager lobbyManager;
    
    @Autowired
    public GameActionController(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }
    
    @GetMapping("/{lobbyId}/status")
    @Operation(
            summary = "Get game status",
            description = "Get the current status of the game for a specific player",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game status retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Game or lobby not found")
            }
    )
    public ResponseEntity<GameStatus> getGameStatus(
            @PathVariable String lobbyId,
            @RequestParam int playerId
    ) {
        GameManager gameManager = lobbyManager.getGameManager(lobbyId);
        if (gameManager == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(gameManager.getStatusFor(playerId));
    }
    
    @PostMapping("/{lobbyId}/play")
    @Operation(
            summary = "Play a card",
            description = "Play a card from a player's hand",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card played successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid move"),
                    @ApiResponse(responseCode = "404", description = "Game or lobby not found")
            }
    )
    public ResponseEntity<ActionResult> playCard(
            @PathVariable String lobbyId,
            @RequestParam int playerId,
            @RequestParam int cardId
    ) {
        GameManager gameManager = lobbyManager.getGameManager(lobbyId);
        if (gameManager == null) {
            return ResponseEntity.notFound().build();
        }
        
        ActionResult result = gameManager.playCard(playerId, cardId);
        
        if (result.getType() == ActionResultType.INVALID_MOVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/{lobbyId}/discard")
    @Operation(
            summary = "Discard a card",
            description = "Discard a card from a player's hand to gain a hint token",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card discarded successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid move"),
                    @ApiResponse(responseCode = "404", description = "Game or lobby not found")
            }
    )
    public ResponseEntity<ActionResult> discardCard(
            @PathVariable String lobbyId,
            @RequestParam int playerId,
            @RequestParam int cardIndex
    ) {
        GameManager gameManager = lobbyManager.getGameManager(lobbyId);
        if (gameManager == null) {
            return ResponseEntity.notFound().build();
        }
        
        ActionResult result = gameManager.discardCard(playerId, cardIndex);
        
        if (result.getType() == ActionResultType.INVALID_MOVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/{lobbyId}/hint")
    @Operation(
            summary = "Give a hint",
            description = "Give a hint to another player about their cards",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Hint given successfully"),
                    @ApiResponse(responseCode = "400", description = "Invalid hint or insufficient hint tokens"),
                    @ApiResponse(responseCode = "404", description = "Game or lobby not found")
            }
    )
    public ResponseEntity<ActionResult> giveHint(
            @PathVariable String lobbyId,
            @RequestParam int fromPlayerId,
            @RequestParam int toPlayerId,
            @RequestParam HintType hintType,
            @RequestParam String hintValue
    ) {
        GameManager gameManager = lobbyManager.getGameManager(lobbyId);
        if (gameManager == null) {
            return ResponseEntity.notFound().build();
        }
        
        // Convert hint value based on hint type
        Object value;
        if (hintType == HintType.COLOR) {
            try {
                value = Card.Color.valueOf(hintValue.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().body(ActionResult.invalid("Invalid color value"));
            }
        } else {
            try {
                value = Integer.parseInt(hintValue);
                if ((Integer)value < 1 || (Integer)value > 5) {
                    return ResponseEntity.badRequest().body(ActionResult.invalid("Invalid card value (must be 1-5)"));
                }
            } catch (NumberFormatException e) {
                return ResponseEntity.badRequest().body(ActionResult.invalid("Invalid hint value format"));
            }
        }
        
        ActionResult result = gameManager.giveHint(fromPlayerId, toPlayerId, hintType, value);
        
        if (result.getType() == ActionResultType.INVALID_MOVE) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
        
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/{lobbyId}/history")
    @Operation(
            summary = "Get game history",
            description = "Get the history of moves in the current game",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game history retrieved successfully"),
                    @ApiResponse(responseCode = "404", description = "Game or lobby not found")
            }
    )
    public ResponseEntity<List<String>> getGameHistory(
            @PathVariable String lobbyId
    ) {
        GameManager gameManager = lobbyManager.getGameManager(lobbyId);
        if (gameManager == null) {
            return ResponseEntity.notFound().build();
        }
        
        return ResponseEntity.ok(gameManager.getGameHistory());
    }
}