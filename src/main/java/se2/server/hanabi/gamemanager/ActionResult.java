package se2.server.hanabi.gamemanager;

public class ActionResult {
    private final ActionResultType type;
    private final String message;

    public ActionResult(ActionResultType type, String message) {
        this.type = type;
        this.message = message;
    }

    public static ActionResult success(String msg) {
        return new ActionResult(ActionResultType.SUCCESS, msg);
    }

    public static ActionResult failure(String msg) {
        return new ActionResult(ActionResultType.FAILURE, msg);
    }

    public static ActionResult invalid(String msg) {
        return new ActionResult(ActionResultType.INVALID_MOVE, msg);
    }

    // Getters & Setters
    public boolean isSuccess() {
        return type == ActionResultType.SUCCESS;
    }

    public String getMessage() {
        return message;
    }

    public ActionResultType getType() {
        return type;
    }
}
