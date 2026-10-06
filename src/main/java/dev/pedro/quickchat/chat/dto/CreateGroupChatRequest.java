package dev.pedro.quickchat.chat.dto;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public record CreateGroupChatRequest(
    @NotBlank(message = "Field 'name' should not be empty")
    String name,
    String iconUrl,
    @NotEmpty(message = "Field 'usersIds' should not be empty")
    @Size(min = 1, max = 100, message = "Groups must have between 1 and 100 user IDs")
    List<@NotBlank(message = "User ID should not be blank") String> usersIds
) {}
