package com.letsroast.service;

import com.letsroast.model.Group;

import java.util.List;

public interface GroupService {
    Group createGroup(String name, String createdBy);
    List<Group> listAllGroups();
    List<Group> listGroupsForUser(String userId);
    void joinGroup(String groupId, String userId);
    boolean isMember(String groupId, String userId);
    Group getGroupById(String groupId);
}

