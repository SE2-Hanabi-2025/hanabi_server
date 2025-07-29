package se2.server.hanabi.services;

import java.security.SecureRandom;

public class LobbyCodeGenerator {
    private static final String CHARACTERS = "ABCDEFGHJKLMNPQRSTUVWXYZ123456789";
    private static final int LENGTH = 6;

    private static final SecureRandom random = new SecureRandom();

    public static String generateLobbyCode() {
        StringBuilder lobbyCode = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            lobbyCode.append(CHARACTERS.charAt(index));
        }
        return lobbyCode.toString();
    }
}
