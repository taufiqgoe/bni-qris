package id.taufiq.bniqris.util;

import java.security.SecureRandom;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class UniqueIdGenerator {
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
    private static final int RANDOM_PART_LENGTH = 16;
    private static final SecureRandom random = new SecureRandom();

    public static String generateRandomId() {
        return generateRandomId(RANDOM_PART_LENGTH);
    }

    public static String generateRandomId(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(CHARACTERS.length());
            sb.append(CHARACTERS.charAt(index));
        }

        return sb.toString();
    }

    public static String generateTimestampId() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        ZonedDateTime now = ZonedDateTime.now(ZoneId.systemDefault());
        return now.format(formatter);
    }
}
