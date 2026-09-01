package dev.pedro.quickchat.user.dto;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateUserRequest(
    @NotBlank(message = "Username must not be empty")
    @Size(min = 3, max = 20, message = "Username must have between 3 and 20 characters")
    String username,

    @NotBlank(message = "Nickname must not be empty")
    @Size(min = 3, max = 30, message = "Nickname must have between 3 and 30 characters")
    String nickname,

    @NotBlank(message = "Password must not be empty")
    @Size(min = 6, message = "Password must have atleast 6 characters")
    String password,

    MultipartFile file
) {}
