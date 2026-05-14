package com.letsroast.service;

import com.letsroast.model.Group;
import com.letsroast.model.UserGroupMembership;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InMemoryGroupService implements GroupService {
    private final Map<String, Group> groups = new HashMap<>();
    private final Set<String> memberships = new HashSet<>(); // stores "userId:groupId"

    @Override
    public Group createGroup(String name, String createdBy) {
        Group group = new Group(name, createdBy);
        groups.put(group.getId(), group);
        // Creator auto-joins
        memberships.add(createdBy + ":" + group.getId());
        return group;
    }

    @Override
    public List<Group> listAllGroups() {
        return new ArrayList<>(groups.values());
    }

    @Override
    public void joinGroup(String groupId, String userId) {
        if (groups.containsKey(groupId)) {
            memberships.add(userId + ":" + groupId);
        }
    }

    @Override
    public boolean isMember(String groupId, String userId) {
        return memberships.contains(userId + ":" + groupId);
    }

    @Override
    public Group getGroupById(String groupId) {
        return groups.get(groupId);
    }
}

