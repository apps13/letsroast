package com.letsroast.api;

import java.time.Instant;

/**
 * Data transfer object for chat messages.
 * Includes the username so the frontend can display who posted the message.
 */
public record ChatMessageDTO(
    String id,
    String userId,
    String username,
    String message,
    Instant createdAt
) {
}

