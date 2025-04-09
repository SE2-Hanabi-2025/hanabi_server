package se2.server.hanabi.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import se2.server.hanabi.model.Lobby;
import se2.server.hanabi.services.LobbyManager;

@RestController
public class LobbyController {
    private final LobbyManager lobbyManager;

    @Autowired
    public LobbyController(LobbyManager lobbyManager) {
        this.lobbyManager = lobbyManager;
    }


    @GetMapping("/create-lobby")
    public String createLobby() {
        return lobbyManager.createLobby();
    }

    @GetMapping("/join-lobby/{id}")
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


}
