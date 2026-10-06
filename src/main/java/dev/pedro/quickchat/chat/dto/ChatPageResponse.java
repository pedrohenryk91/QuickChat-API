package dev.pedro.quickchat.chat.dto;

import java.util.List;

public record ChatPageResponse(
    List<ChatResponse> content,
    String nextCursor,
    boolean hasMore
) {}
