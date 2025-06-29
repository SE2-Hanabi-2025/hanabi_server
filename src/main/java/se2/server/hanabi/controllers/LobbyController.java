package se2.server.hanabi.controllers;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
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
import se2.server.hanabi.services.LobbyManager;
import se2.server.hanabi.util.GameRules;

import java.util.List;
import java.util.stream.Collectors;

import java.util.Map;
import java.util.HashMap;

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
                    @Parameter(name = "name", description = "The name of the player joining the lobby", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "Anonymous"),

                    @Parameter(name = "avatarResID", description = "The avatar resource ID of the player", example = "2131230890")
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully joined the lobby",
                            content = @io.swagger.v3.oas.annotations.media.Content(
                                    mediaType = "text/plain",
                                    schema = @Schema(type = "string", example = "Joined lobby: abc123 PlayerID: 4")
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
            @RequestParam(defaultValue = "Anonymous") String name,
            @RequestParam(defaultValue = "0") int avatarResID
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

        int playerId = lobbyManager.joinLobby(id, name, avatarResID);
        if (playerId != -1) {
            return ResponseEntity.ok("Joined lobby: "+id+" PlayerID: "+ playerId);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Unknown error while joining lobby.");
        }
    }
    @GetMapping("/leave-lobby/{lobbyId}/{playerId}")
    @Operation(
            summary = "Player leaves lobby",
            description = "Player leaves lobby; Server removes player from the list",
            parameters = {
                    @Parameter(name = "lobbyId", description = "Lobby ID", required = true, in = ParameterIn.PATH),
                            @Parameter(name = "playerID", description = "Player ID", required = true, in = ParameterIn.PATH, schema = @Schema(type = "integer"))
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Left lobby successfully"),
                    @ApiResponse(responseCode = "404", description = "Lobby/Player not found")
            }
    )

    public ResponseEntity<String> leaveLobby(
            @PathVariable String lobbyId,
            @PathVariable int playerId
    ) {
        boolean success = lobbyManager.leaveLobby(lobbyId, playerId);
        if (success){
            return ResponseEntity.ok("Player" + playerId + "left lobby successfully");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Failed to leave lobby");
        }
    }


    @GetMapping("/start-game/{id}")
    @Operation(
            summary = "Start a game in a lobby",
            description = "Starts a new game with all players currently in the lobby. Requires at least 2 players.",
            parameters = {
                    @Parameter(name = "id", description = "The unique identifier of the lobby", required = true, in = io.swagger.v3.oas.annotations.enums.ParameterIn.PATH),
                    @Parameter(name = "isCasualMode", description = "Decides the game mode", in = io.swagger.v3.oas.annotations.enums.ParameterIn.QUERY, example = "true"),
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
    public ResponseEntity<String> startGame(
        @PathVariable String id, 
        @RequestParam Boolean isCasualMode
    ) {
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

        boolean success = lobbyManager.startGame(id, isCasualMode);
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

    public ResponseEntity<List<Map<String, Object>>> getPlayersInLobby(@PathVariable String id) {
        Lobby lobby = lobbyManager.getLobby(id);
        if (lobby == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        List<Map<String, Object>> players = lobby.getPlayers().stream()
                .map(player -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", player.getName());
                    map.put("avatarResID", player.getAvatarResID());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(players);
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
