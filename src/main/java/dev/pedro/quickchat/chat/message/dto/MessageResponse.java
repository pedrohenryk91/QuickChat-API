package dev.pedro.quickchat.chat.message.dto;

import java.time.LocalDateTime;

public record MessageResponse (
    Long id,
    String content,
    String authorNickname,
    String chatId,
    LocalDateTime createdAt
) {

}
