package com.letsroast.model;

import java.time.Instant;
import java.util.UUID;

public class Group {
    private String id;
    private String name;
    private String createdBy;
    private Instant createdAt;

    public Group(String name, String createdBy) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.createdBy = createdBy;
        this.createdAt = Instant.now();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    @Override
    public String toString() {
        return "Group{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", createdBy='" + createdBy + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}

