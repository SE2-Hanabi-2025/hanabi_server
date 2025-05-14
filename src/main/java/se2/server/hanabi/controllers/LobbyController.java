package se2.server.hanabi.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se2.server.hanabi.model.Lobby;
import se2.server.hanabi.model.Player;
import se2.server.hanabi.services.LobbyManager;
import se2.server.hanabi.util.GameRules;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@Tag(name = "Lobby API", description = "Endpoints to manage lobbies, such as creating and joining a lobby.")
public class LobbyController {
    private final LobbyManager lobbyManager;

    @Autowired
    public LobbyController(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }


    @GetMapping("/create-lobby")
    @Operation(
            summary = "Create a new lobby",
            description = "This endpoint allows users to create a new lobby. Each lobby gets a unique id.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Lobby created successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Lobby created with ID: abc123")
                            )),
                    @ApiResponse(responseCode = "500", description = "Server error")
            }
    )
    public String createLobby() {
        return lobbyManager.createLobby();
    }

    @GetMapping("/join-lobby/{id}")
    @Operation(
            summary = "Join a lobby",
            description = "This endpoint allows a player to join an existing lobby using the lobby ID. If the lobby is full or the game has already started, it returns appropriate error messages.",
            parameters = {
                    @Parameter(name = "id", description = "The unique identifier of the lobby", required = true, in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH),
                    @Parameter(name = "name", description = "The name of the player joining the lobby", required = false, in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "Anonymous")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully joined the lobby",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Joined lobby: abc123")
                            )),
                    @ApiResponse(responseCode = "404", description = "Lobby not found",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Lobby not found.")
                            )),
                    @ApiResponse(responseCode = "409", description = "Game already started",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Game already started.")
                            )),
                    @ApiResponse(responseCode = "400", description = "Lobby is full",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Lobby is full.")
                            )),
                    @ApiResponse(responseCode = "500", description = "Internal server error",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Unknown error while joining lobby.")
                            ))
            }
    )
    public ResponseEntity<String> joinLobby(
            @PathVariable String id,
            @RequestParam(defaultValue = "Anonymous") String name
    ) {
        Lobby lobby = lobbyManager.getLobby(id);

        if (lobby == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lobby not found.");
        }

        if (lobby.isGameStarted()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Game already started.");
        }

        if (lobby.getPlayers().size() >= 5) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Lobby is full.");
        }

        boolean success = lobbyManager.joinLobby(id, name);
        if (success) {
            return ResponseEntity.ok("Joined lobby: " + id);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unknown error while joining lobby.");
        }
    }
    @GetMapping("/start-game/{id}")
    @Operation(
            summary = "Start a game in a lobby",
            description = "Starts a new game with all players currently in the lobby. Requires at least 2 players.",
            parameters = {
                    @Parameter(name = "id", description = "The unique identifier of the lobby", required = true, in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game started successfully",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Game started successfully")
                            )),
                    @ApiResponse(responseCode = "404", description = "Lobby not found",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Lobby not found")
                            )),
                    @ApiResponse(responseCode = "400", description = "Not enough players or game already started",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Cannot start game: Insufficient players or game already started")
                            ))
            }
    )
    public ResponseEntity<String> startGame(@PathVariable String id) {
        Lobby lobby = lobbyManager.getLobby(id);
        
        if (lobby == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Lobby not found");
        }
        
        if (lobby.isGameStarted()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Game already started");
        }
        
        if (lobby.getPlayers().size() < GameRules.MIN_PLAYERS) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Not enough players to start game (minimum 2)");
        }
        
        boolean success = lobbyManager.startGame(id);
        if (success) {
            return ResponseEntity.ok("Game started successfully");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Cannot start game: Unknown error");
        }
    }

    @GetMapping("/lobby/{id}/players")
    @Operation(
            summary = "Retrieve players in a lobby",
            description = "Returns the list of players in a specific lobby",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Players successfully recalled"),
                    @ApiResponse(responseCode = "404", description = "Lobby not found")
            }
    )
    public ResponseEntity<List<String>> getPlayersInLobby(@PathVariable String id) {
        Lobby lobby = lobbyManager.getLobby(id);
        if (lobby == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<String> playerNames = lobby.getPlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(playerNames);
    }

    
    @GetMapping("/lobbies")
    @Operation(
            summary = "List all lobbies",
            description = "Returns a list of all available lobbies",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of lobbies retrieved successfully")
            }
    )
    public ResponseEntity<java.util.Collection<Lobby>> getAllLobbies() {
        return ResponseEntity.ok(lobbyManager.getAllLobbies());
    }

    @GetMapping("/start-game/{id}/status")
    @Operation(
            summary = "Check if the game has started in a lobby",
            description = "Returns true if the game in the specified lobby has started, false otherwise.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Game status retrieved",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "application/json",
                                    schema = @Schema(type = "boolean", example = "true")
                            )),
                    @ApiResponse(responseCode = "404", description = "Lobby not found")
            }
    )
    public ResponseEntity<Boolean> isGameStarted(@PathVariable String id) {
        Lobby lobby = lobbyManager.getLobby(id);
        if (lobby == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(lobby.isGameStarted());
    }
}
