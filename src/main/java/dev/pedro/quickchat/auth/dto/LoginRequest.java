package dev.pedro.quickchat.auth.dto;

import jakarta.validation.constraints.NotBlank;

public record LoginRequest(
    @NotBlank(message = "Field 'username' can not be empty")
    String username,

    @NotBlank(message = "Field 'password' can not be empty")
    String password
) {}
