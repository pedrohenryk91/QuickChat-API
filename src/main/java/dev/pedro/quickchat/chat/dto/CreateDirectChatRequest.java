package dev.pedro.quickchat.chat.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateDirectChatRequest(
    @NotBlank(message = "Field 'receiverUserId' can not be blank")
    String receiverUserId
) {}
