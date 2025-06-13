package se2.server.hanabi.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonAlias;
import se2.server.hanabi.game.HintType;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameActionMessage {
    
    public enum ActionType {
        PLAY,
        DISCARD,
        HINT,
        DEFUSE // Added for cheat action
    }
    
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

    // Default constructor for JSON deserialization
    public GameActionMessage() {
    }
    
    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ActionType getActionType() {
        return actionType;
    }

    public void setActionType(ActionType actionType) {
        this.actionType = actionType;
    }

    public String getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(String lobbyId) {
        this.lobbyId = lobbyId;
    }

    public Integer getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Integer playerId) {
        this.playerId = playerId;
    }

    public Integer getCardIndex() {
        return cardIndex;
    }

    public void setCardIndex(Integer cardIndex) {
        this.cardIndex = cardIndex;
    }

    public Integer getToPlayerId() {
        return toPlayerId;
    }

    public void setToPlayerId(Integer toPlayerId) {
        this.toPlayerId = toPlayerId;
    }

    public HintType getHintType() {
        return hintType;
    }

    public void setHintType(HintType hintType) {
        this.hintType = hintType;
    }

    public String getHintValue() {
        return hintValue;
    }

    public void setHintValue(String hintValue) {
        this.hintValue = hintValue;
    }
}
