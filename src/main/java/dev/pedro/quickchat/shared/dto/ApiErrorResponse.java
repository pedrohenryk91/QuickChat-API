package dev.pedro.quickchat.shared.dto;

import java.time.LocalDateTime;

public record ApiErrorResponse(
    int status,
    String message,
    String path,
    LocalDateTime timestamp
) {
    public static ApiErrorResponse simple(int status, String message, String path) {
        return new ApiErrorResponse(status, message, path, LocalDateTime.now());
    }
}
