package se2.server.hanabi.game;

public class ValueHintAndRemainingTurns {
    private int value;
    private int numTurns;
    

    public ValueHintAndRemainingTurns(int value, int numTurns) {
        this.value = value;
        this.numTurns = numTurns;
    }

    public int getValue() {
        return value;
    }

    public int getNumTurns() {
        return numTurns;
    }

    public void setNumTurns(int numTurns) {
        this.numTurns = numTurns;
    }

}
