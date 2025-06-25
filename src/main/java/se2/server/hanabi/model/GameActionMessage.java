package se2.server.hanabi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Getter;
import lombok.Setter;
import se2.server.hanabi.game.HintType;

@Setter
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameActionMessage {
    
    public enum ActionType {
        PLAY,
        DISCARD,
        HINT,
        CHEAT,
        DEFUSE, 
        ADD_STRIKE,
        DEFUSE_ATTEMPT
    }


    @JsonProperty("type")
    private String type;
    
    @JsonProperty("action")
    @JsonAlias({"type"})
    private ActionType actionType;
    
    @JsonProperty("lobbyId")
    private String lobbyId;
    
    @JsonProperty("playerId")
    private Integer playerId;
    
    @JsonProperty("cardIndex")
    private Integer cardIndex;
    
    @JsonProperty("toPlayerId")
    private Integer toPlayerId;
    
    @JsonProperty("hintType")
    private HintType hintType;
    
    @JsonProperty("hintValue")
    private String hintValue;
    
    @JsonProperty("sequence")
    private java.util.List<String> sequence;

    @JsonProperty("proximity")
    private String proximity;

    // Default constructor for JSON deserialization
    public GameActionMessage() {
        // This empty constructor is intentionally left blank
        // It's required by Jackson for JSON deserialization
        // Jackson uses this no-args constructor to create an instance 
        // and then populate fields from JSON
    }

}
