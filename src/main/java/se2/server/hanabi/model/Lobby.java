package se2.server.hanabi.model;

import lombok.Getter;

import java.util.*;

public class Lobby {
    @Getter
    private final String id;
    @Getter
    private List<Player> players = new ArrayList<>();
    private boolean isGameStarted;

    public Lobby() {
        this.id = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
        this.isGameStarted = false;
    }

    public boolean isGameStarted() {
        return isGameStarted;
    }
    public void startGame(){
        this.isGameStarted = true;
    }
}
