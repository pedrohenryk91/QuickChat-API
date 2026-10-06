package dev.pedro.quickchat.message.dto;

public record SendMessageRequest(
    String chatId,
    String tempId,
    String content
) {}
