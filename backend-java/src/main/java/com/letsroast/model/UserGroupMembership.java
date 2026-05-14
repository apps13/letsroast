package com.letsroast.model;

import java.time.Instant;

public class UserGroupMembership {
    private String userId;
    private String groupId;
    private Instant joinedAt;

    public UserGroupMembership(String userId, String groupId) {
        this.userId = userId;
        this.groupId = groupId;
        this.joinedAt = Instant.now();
    }

    public String getUserId() {
        return userId;
    }

    public String getGroupId() {
        return groupId;
    }

    public Instant getJoinedAt() {
        return joinedAt;
    }

    @Override
    public String toString() {
        return "UserGroupMembership{" +
                "userId='" + userId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", joinedAt=" + joinedAt +
                '}';
    }
}

