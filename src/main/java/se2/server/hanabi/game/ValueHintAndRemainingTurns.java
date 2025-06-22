package se2.server.hanabi.game;

import lombok.Getter;
import lombok.Setter;

@Getter
public class ValueHintAndRemainingTurns {
    private final int value;
    @Setter
    private int numTurns;
    

    public ValueHintAndRemainingTurns(int value, int numTurns) {
        this.value = value;
        this.numTurns = numTurns;
    }

}
