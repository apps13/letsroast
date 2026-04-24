package com.letsroast.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class Post {

    private UUID id;
    private UUID userId;
    private String content;
    private LocalDateTime timestamp;

    public Post(UUID userId, String content) {
        this.id = UUID.randomUUID();
        this.userId = userId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
