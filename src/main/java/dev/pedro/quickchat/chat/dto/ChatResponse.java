package dev.pedro.quickchat.chat.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatResponse(
    String id,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,

    @Nullable
    @Schema(description = "Chat name (null if chat is direct)", nullable = true)
    String name
) {}
