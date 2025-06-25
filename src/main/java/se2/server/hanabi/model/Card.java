package se2.server.hanabi.model;

import lombok.Getter;

@Getter
public class Card {
    private final int value;
    private final Color color;
    private final int id;
    private static int nextID = 0;

    public Card(int value, Color color) {
        this.value = value;
        this.color = color;
        this.id = nextID++;
    }

    public static void resetNextID() {
        nextID = 0;
    }

    @Override
    public String toString() {
        return "Card{" + "value=" + value + ", color=" + color + ", id=" + id + '}';
    }

    public enum Color {
        RED,
        GREEN,
        BLUE,
        YELLOW,
        WHITE
    }
}
