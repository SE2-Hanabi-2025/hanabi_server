package se2.server.hanabi.model;

public class Player {
    private String name;
    private final int id;
    private static int nextId = 0;

    public Player(String name) {
        this.name = name;
        this.id = nextId++;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }
}
