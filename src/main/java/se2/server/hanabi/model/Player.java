package se2.server.hanabi.model;

public class Player {
    private String name;
    private final int id;
    private static int nextId = 0;

    private int avatarResID;
 
    public Player(String name, int avatarResID) {
        this.name = name;
        this.id = nextId++;

        this.avatarResID = avatarResID;
    }

    public Player(String name) {
        this(name,0);
    }

    public Player(int id) {
        this.id = id;
        this.name = "Player" + id; // Default name based on ID

        this.avatarResID = 0;
    }

    public String getName() {
        return name;
    }

    public int getAvatarResID() {
        return avatarResID;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
}
