package dev.pedro.quickchat.message.dto;

import java.time.LocalDateTime;
import java.util.Optional;

import jakarta.annotation.Nullable;

public record MessageResponse (
    Long id,
    String chatId,
    String content,
    String authorId,
    String authorName,
    LocalDateTime createdAt,
    @Nullable String tempId
) {
    public Optional<String> getTempIdOptional() {
        return Optional.ofNullable(tempId);
    }
}
