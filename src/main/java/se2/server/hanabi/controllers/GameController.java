package se2.server.hanabi.controllers;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Deck;

@RestController
@Tag(name = "Game API", description = "Endpoints to manage the game and its operations like starting a game, drawing a card, etc.")
public class GameController {
    private Deck deck = new Deck();  

    
    @GetMapping("/connect")
    @Operation(
            summary = "Establish a connection with the server",
            description = "This endpoint establishes a connection with the server. Returns a confirmation message.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Connection established",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Connection established with the server!")
                            )),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    public String connect() {
        return "Connection established with the server!";
    }

    @GetMapping("/game/start")
    @Operation(
            summary = "Start a new game",
            description = "This endpoint resets the deck and starts a new game by initializing a new deck of cards. Returns a confirmation message.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game started successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Game started! New deck created.")
                            )),
                    @ApiResponse(responseCode = "400", description = "Bad Request")
            }
    )
    public String startGame() {
        deck = new Deck();  // Reset the deck
        return "Game started! New deck created.";
    }

    @GetMapping("/game/draw")
    @Operation(
            summary = "Draw a card from the deck",
            description = "This endpoint allows the user to draw a card from the deck. If the deck is empty, it will return a message indicating there are no more cards left.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Card drawn successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Drew a card: Ace of Spades")
                            )),
                    @ApiResponse(responseCode = "400", description = "Bad Request"),
                    @ApiResponse(responseCode = "404", description = "Deck is empty",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "No more cards in the deck!")
                            ))
            }
    )
    public String drawCard() {
        Card card = deck.drawCard();
        return (card != null) ? "Drew a card: " + card.getValue() : "No more cards in the deck!";
    }

    @GetMapping("/status")
    @Operation(
            summary = "Get server status",
            description = "This endpoint checks the status of the server. Returns a confirmation message.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Server is running",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Server is running and ready to accept requests.")
                            )),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    public String getServerStatus() {
        return "Server is running and ready to accept requests.";
    }

}