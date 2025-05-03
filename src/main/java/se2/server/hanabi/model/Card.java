package se2.server.hanabi.model;

public class Card {
    private int value;
    private Color color;

    public Card(int value, Color color) {
        this.value = value;
        this.color = color;
    }

    public int getValue() {
        return value;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
<<<<<<< HEAD
        return "Card{" + "value=" + value + ", color=" + color + '}';
    }

    public enum Color{
        RED,
        GREEN,
        BLUE,
        YELLOW,
        WHITE
=======
        return "Card{" + "value=" + value + " color=" + color + '}';
>>>>>>> origin/development
    }
}
