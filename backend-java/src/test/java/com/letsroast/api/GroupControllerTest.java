package com.letsroast.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letsroast.model.Group;
import com.letsroast.model.User;
import com.letsroast.service.AuthSessionService;
import com.letsroast.service.GroupService;
import com.letsroast.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(GroupController.class)
class GroupControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    GroupService groupService;

    @MockBean
    UserService userService;

    @MockBean
    AuthSessionService authSessionService;

    private static final Cookie SESSION = new Cookie("letsroast_session", "valid-session");

    private User fakeUser() {
        return new User("alice", "hashed");
    }

    private Group fakeGroup(String name) {
        return new Group(name, "user-1");
    }

    // ── Create Group ──────────────────────────────────────────────────────────

    @Test
    void createGroup_authenticated_returns200WithGroup() throws Exception {
        User user = fakeUser();
        Group group = fakeGroup("roast-squad");

        when(authSessionService.getUserIdFromSession("valid-session")).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(groupService.createGroup(anyString(), anyString())).thenReturn(group);

        mockMvc.perform(post("/api/groups")
                        .cookie(SESSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupController.CreateGroupRequest("roast-squad"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("roast-squad"));
    }

    @Test
    void createGroup_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/groups")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupController.CreateGroupRequest("roast-squad"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void createGroup_blankName_returns400() throws Exception {
        User user = fakeUser();
        when(authSessionService.getUserIdFromSession("valid-session")).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(user);

        mockMvc.perform(post("/api/groups")
                        .cookie(SESSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new GroupController.CreateGroupRequest("  "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── List All Groups ───────────────────────────────────────────────────────

    @Test
    void listGroups_authenticated_returnsGroupList() throws Exception {
        User user = fakeUser();
        when(authSessionService.getUserIdFromSession("valid-session")).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(groupService.listAllGroups()).thenReturn(List.of(fakeGroup("g1"), fakeGroup("g2")));

        mockMvc.perform(get("/api/groups").cookie(SESSION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void listGroups_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/groups"))
                .andExpect(status().isUnauthorized());
    }

    // ── List My Groups ────────────────────────────────────────────────────────

    @Test
    void listMyGroups_authenticated_returnsOnlyUsersGroups() throws Exception {
        User user = fakeUser();
        when(authSessionService.getUserIdFromSession("valid-session")).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(groupService.listGroupsForUser(user.getId())).thenReturn(List.of(fakeGroup("mine")));

        mockMvc.perform(get("/api/groups/mine").cookie(SESSION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].name").value("mine"));
    }

    // ── Join Group ────────────────────────────────────────────────────────────

    @Test
    void joinGroup_authenticated_returns200() throws Exception {
        User user = fakeUser();
        Group group = fakeGroup("open-group");

        when(authSessionService.getUserIdFromSession("valid-session")).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(groupService.getGroupById(group.getId())).thenReturn(group);

        mockMvc.perform(post("/api/groups/" + group.getId() + "/join").cookie(SESSION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("joined group"));
    }

    @Test
    void joinGroup_groupNotFound_returns404() throws Exception {
        User user = fakeUser();
        when(authSessionService.getUserIdFromSession("valid-session")).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(user);
        when(groupService.getGroupById(anyString())).thenReturn(null);

        mockMvc.perform(post("/api/groups/ghost-group-id/join").cookie(SESSION))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("group not found"));
    }
}

