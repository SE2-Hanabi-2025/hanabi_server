package se2.server.hanabi.services;

import java.security.SecureRandom;

public class LobbyCodeGenerator {
    private static final String REGEX = "^[A-NP-Z1-9]+$";
    private static final int length = 6;

    private static final SecureRandom random = new SecureRandom();

    public static String generateLobbyCode() {
        StringBuilder lobbyCode = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(REGEX.length());
            lobbyCode.append(REGEX.charAt(index));
        }
        return lobbyCode.toString();
    }
}
