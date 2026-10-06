package dev.pedro.quickchat.chat.dto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

public record ChatCursor(LocalDateTime beforeDate, String beforeId) {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public String toBase64() {
        String raw = beforeDate.format(FORMATTER) + "|" + beforeId;
        return Base64.getEncoder().encodeToString(raw.getBytes(StandardCharsets.UTF_8));
    }

    public static ChatCursor fromBase64(String base64Cursor) {
        if(base64Cursor == null || base64Cursor.isBlank()) {
            return null;
        }

        try {
            byte[] decodedBytes = Base64.getDecoder().decode(base64Cursor);
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            String[] parts = decodedString.split("\\|", 2);
            if(parts.length < 2) {
                return null;
            }

            LocalDateTime date = LocalDateTime.parse(parts[0], FORMATTER);

            return new ChatCursor(date, parts[1]);
        } catch (Exception e) {
            return null;
        }
    }
}
