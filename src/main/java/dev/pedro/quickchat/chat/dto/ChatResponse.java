package dev.pedro.quickchat.chat.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ChatResponse(
    String id,
    LocalDateTime updatedAt,
    String iconUrl,
    String name
) {}
