package com.letsroast.model;

import java.time.Instant;
import jakarta.persistence.*;

@Entity
@Table(name = "user_group_membership")
public class UserGroupMembership {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String groupId;

    @Column(nullable = false)
    private Instant joinedAt;

    public UserGroupMembership() {
    }

    public UserGroupMembership(String userId, String groupId) {
        this.userId = userId;
        this.groupId = groupId;
        this.joinedAt = Instant.now();
    }

    public Long getId() {
        return id;
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

