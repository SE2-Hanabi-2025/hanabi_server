package se2.server.hanabi.model;

import lombok.Getter;
import lombok.Setter;

@Getter
public class Player {
    @Setter
    private String name;
    private final int id;
    private static int nextId = 0;

    private int avatarResID = 0;
 
    public Player(String name, int avatarResID) {
        this.name = name;
        this.id = nextId++;

        this.avatarResID = avatarResID;
    }

    public Player(String name) {
        this(name,0);
    }


}
