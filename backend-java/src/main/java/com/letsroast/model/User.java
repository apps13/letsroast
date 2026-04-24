package com.letsroast.model;

import java.time.LocalDateTime;
import java.util.UUID;

public class User {

    private UUID id;
    private String username;
    private String passwordHash;
    private LocalDateTime createdAt;

    public User(String username, String passwordHash) {
        this.id = UUID.randomUUID();
        this.username = username;
        this.passwordHash = passwordHash;
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
