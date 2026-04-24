package com.letsroast.model;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all domain entities.
 * Provides common fields: id and createdAt.
 */
public abstract class Entity {
    
    protected UUID id;
    protected LocalDateTime createdAt;

    protected Entity() {
        this.id = UUID.randomUUID();
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
