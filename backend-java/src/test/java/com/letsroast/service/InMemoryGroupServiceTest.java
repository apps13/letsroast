package com.letsroast.service;

import com.letsroast.model.Group;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryGroupServiceTest {

    private InMemoryGroupService groupService;

    @BeforeEach
    void setUp() {
        groupService = new InMemoryGroupService();
    }

    @Test
    void createGroup_returnsGroupWithCorrectName() {
        Group group = groupService.createGroup("roast-squad", "user-1");

        assertThat(group).isNotNull();
        assertThat(group.getName()).isEqualTo("roast-squad");
        assertThat(group.getId()).isNotBlank();
    }

    @Test
    void createGroup_creatorIsAutoAddedAsMember() {
        Group group = groupService.createGroup("cool-gang", "user-1");

        assertThat(groupService.isMember(group.getId(), "user-1")).isTrue();
    }

    @Test
    void joinGroup_newMember_isMember() {
        Group group = groupService.createGroup("chat-room", "user-1");

        groupService.joinGroup(group.getId(), "user-2");

        assertThat(groupService.isMember(group.getId(), "user-2")).isTrue();
    }

    @Test
    void joinGroup_nonExistentGroup_doesNotThrow() {
        groupService.joinGroup("fake-group-id", "user-1");

        assertThat(groupService.isMember("fake-group-id", "user-1")).isFalse();
    }

    @Test
    void isMember_nonMember_returnsFalse() {
        Group group = groupService.createGroup("exclusive", "user-1");

        assertThat(groupService.isMember(group.getId(), "user-2")).isFalse();
    }

    @Test
    void listAllGroups_returnsAllCreatedGroups() {
        groupService.createGroup("group-a", "user-1");
        groupService.createGroup("group-b", "user-2");

        List<Group> groups = groupService.listAllGroups();

        assertThat(groups).hasSize(2);
    }

    @Test
    void listGroupsForUser_returnsOnlyUserGroups() {
        groupService.createGroup("user1-group", "user-1");
        Group shared = groupService.createGroup("shared-group", "user-2");
        groupService.joinGroup(shared.getId(), "user-1");

        List<Group> user1Groups = groupService.listGroupsForUser("user-1");

        assertThat(user1Groups).hasSize(2);
    }

    @Test
    void listGroupsForUser_userNotInAnyGroup_returnsEmpty() {
        groupService.createGroup("someone-elses-group", "user-1");

        List<Group> groups = groupService.listGroupsForUser("user-2");

        assertThat(groups).isEmpty();
    }

    @Test
    void getGroupById_returnsCorrectGroup() {
        Group created = groupService.createGroup("findme", "user-1");

        Group found = groupService.getGroupById(created.getId());

        assertThat(found).isNotNull();
        assertThat(found.getName()).isEqualTo("findme");
    }

    @Test
    void getGroupById_unknownId_returnsNull() {
        assertThat(groupService.getGroupById("nonexistent")).isNull();
    }
}

