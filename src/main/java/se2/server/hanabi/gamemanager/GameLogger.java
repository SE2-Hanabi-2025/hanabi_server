package se2.server.hanabi.gamemanager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GameLogger {

    public enum LogLevel {
        INFO, WARN, ERROR
    }

    private final List<String> history = new ArrayList<>();

    public void log(LogLevel level, String message) {
        String entry = "[" + level + "] " + message;
        history.add(entry);
        System.out.println(entry);
    }

    public void info(String message) {
        log(LogLevel.INFO, message);
    }

    public void warn(String message) {
        log(LogLevel.WARN, message);
    }

    public void error(String message) {
        log(LogLevel.ERROR, message);
    }

    public List<String> getHistory() {
        return Collections.unmodifiableList(history);
    }

    public void clear() {
        history.clear();
    }
}

