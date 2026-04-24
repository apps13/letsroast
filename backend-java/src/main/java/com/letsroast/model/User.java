package com.letsroast.model;

public class User extends Entity {

    private String username;
    private String passwordHash;

    public User(String username, String passwordHash) {
        super();
        this.username = username;
        this.passwordHash = passwordHash;
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
