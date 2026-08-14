package dev.pedro.quickchat.user.dto;

public record UserResponse(
    String username,
    String nickname,
    String iconUrl,
    String userId
) {}
