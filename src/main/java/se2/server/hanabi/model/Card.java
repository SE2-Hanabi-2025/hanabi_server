package se2.server.hanabi.model;

public class Card {
    private int value;
    private Color color;
    private final int id; // Change UUID to an integer-based ID
    private static int nextID = 0; // Static counter for generating unique IDs

    public Card(int value, Color color) {
        this.value = value;
        this.color = color;
        this.id = nextID++; // Generate a unique integer ID for each card
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id; // Getter for the unique ID
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
