package com.letsroast.model;

import java.util.UUID;

public class Post extends Entity {

    private UUID userId;
    private String content;

    public Post(UUID userId, String content) {
        super();
        this.userId = userId;
        this.content = content;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getContent() {
        return content;
    }
}
