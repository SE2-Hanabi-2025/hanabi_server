package se2.server.hanabi.game;

import lombok.Getter;
import lombok.Setter;
import se2.server.hanabi.model.Card;
import se2.server.hanabi.model.Card.Color;

@Getter
public class ColorHintAndRemainingTurns {
    private final Card.Color color;
    @Setter
    private int numTurns;
    

    public ColorHintAndRemainingTurns(Color color, int numTurns) {
        this.color = color;
        this.numTurns = numTurns;
    }

}
