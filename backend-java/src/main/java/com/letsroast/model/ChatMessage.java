package com.letsroast.model;

import java.time.Instant;
import java.util.UUID;

public class ChatMessage {
    private String id;
    private String groupId;
    private String userId;
    private String message;
    private Instant createdAt;

    public ChatMessage(String groupId, String userId, String message) {
        this.id = UUID.randomUUID().toString();
        this.groupId = groupId;
        this.userId = userId;
        this.message = message;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getUserId() {
        return userId;
    }

    public String getMessage() {
        return message;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "ChatMessage{" +
                "id='" + id + '\'' +
                ", groupId='" + groupId + '\'' +
                ", userId='" + userId + '\'' +
                ", message='" + message + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

