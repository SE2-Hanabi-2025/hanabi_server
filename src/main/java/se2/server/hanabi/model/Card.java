package se2.server.hanabi.model;

public class Card {
    private int value;

    public Card(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "Card{" + "value=" + value + '}';
    }
}
