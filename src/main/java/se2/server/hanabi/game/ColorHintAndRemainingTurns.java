package se2.server.hanabi.game;

import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Card.Color;

public class ColorHintAndRemainingTurns {
    private Card.Color color;
    private int numTurns;
    

    public ColorHintAndRemainingTurns(Color color, int numTurns) {
        this.color = color;
        this.numTurns = numTurns;
    }

    public Card.Color getColor() {
        return color;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public void setNumTurns(int numTurns) {
        this.numTurns = numTurns;
    }
}
