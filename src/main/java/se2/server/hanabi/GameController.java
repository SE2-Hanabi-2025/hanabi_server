package se2.server.hanabi;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GameController {
    @GetMapping("/connect")
    public String connect() {
        return "Connection established with the server!";
    }

    @GetMapping("/start-game")
    public String startGame() {
        return "Game started!";
    }

    @GetMapping("/status")
    public String getStatus() {
        return "Server is running!";
    }

    @GetMapping("/join-lobby/12345")
    public String joinLobby() {
        return "Joined the lobby!";
    }

}
