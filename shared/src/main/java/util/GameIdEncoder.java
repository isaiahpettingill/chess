package util;

import java.util.Random;

public final class GameIdEncoder {
    private static final int OBFUSCATION_KEY = 479001599;
    private static final int LEFT_PADDING = 4;
    private static final int RIGHT_PADDING = 8;
    private static final Random RANDOM = new Random();

    public static String encode(int gameId) {
        final var sb = new StringBuilder();
        final var new_key = gameId ^ OBFUSCATION_KEY;
        final var key_string = Integer.toString(new_key, 36);
        for (int i = 0; i < LEFT_PADDING; i++) {
            final var thing = RANDOM.nextInt(0, 35);
            sb.append(Integer.toString(thing, 36));
        }
        sb.append(key_string);
        for (int i = 0; i < RIGHT_PADDING; i++) {
            final var junk = RANDOM.nextInt(0, 35);
            sb.append(Integer.toString(junk, 36));
        }
        return sb.toString();
    }

    public static int decode(String gameId) {
        final var substr = gameId.substring(LEFT_PADDING, gameId.length() - RIGHT_PADDING);
        final var integer = Integer.parseInt(substr, 36);
        return integer ^ OBFUSCATION_KEY;
    }
}
