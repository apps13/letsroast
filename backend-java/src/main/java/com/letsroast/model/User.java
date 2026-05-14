package com.letsroast.model;

import java.time.Instant;
import java.util.UUID;

public class User {
    private String id;
    private String username;
    private Instant createdAt;

    public User(String username) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "User{" +
                "id='" + id + '\'' +
                ", username='" + username + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

