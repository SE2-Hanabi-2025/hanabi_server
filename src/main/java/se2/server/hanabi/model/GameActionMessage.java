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
        CHEAT, // Added CHEAT action type
        DEFUSE, 
        ADD_STRIKE,
        DEFUSE_ATTEMPT // New action type for cheat validation
    }

    // Getters and Setters
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("action")
    @JsonAlias({"type"}) // Allow the "type" field to also map to this property
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
