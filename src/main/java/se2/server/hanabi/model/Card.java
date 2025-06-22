package se2.server.hanabi.model;

import lombok.Getter;

@Getter
public class Card {
    private final int value;
    private final Color color;
    // Getter for the unique ID
    private final int id; // Change UUID to an integer-based ID
    private static int nextID = 0; // Static counter for generating unique IDs

    public Card(int value, Color color) {
        this.value = value;
        this.color = color;
        this.id = nextID++; // Generate a unique integer ID for each card
    }

    public static void resetNextID() {
        nextID = 0;
    }

    @Override
    public String toString() {
        return "Card{" + "value=" + value + ", color=" + color + ", id=" + id + '}'; // Include ID in toString
    }

    public enum Color {
        RED,
        GREEN,
        BLUE,
        YELLOW,
        WHITE
    }
}
