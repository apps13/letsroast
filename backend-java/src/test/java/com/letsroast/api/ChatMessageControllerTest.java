package com.letsroast.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letsroast.model.Group;
import com.letsroast.model.User;
import com.letsroast.service.AuthSessionService;
import com.letsroast.service.ChatMessageService;
import com.letsroast.service.GroupService;
import com.letsroast.service.UserService;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChatMessageController.class)
class ChatMessageControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    ChatMessageService chatMessageService;

    @MockBean
    GroupService groupService;

    @MockBean
    UserService userService;

    @MockBean
    AuthSessionService authSessionService;

    private static final Cookie SESSION = new Cookie("letsroast_session", "valid-session");
    private static final String GROUP_ID = "group-123";

    private User fakeUser() {
        return new User("alice", "hashed");
    }

    private Group fakeGroup() {
        return new Group("roast-squad", "user-1");
    }

    private void setupAuthenticatedUser(User user) {
        when(authSessionService.getUserIdFromSession("valid-session")).thenReturn(user.getId());
        when(userService.getUserById(user.getId())).thenReturn(user);
    }

    // ── Post Message ──────────────────────────────────────────────────────────

    @Test
    void postMessage_memberOfGroup_returns200() throws Exception {
        User user = fakeUser();
        setupAuthenticatedUser(user);
        when(groupService.getGroupById(GROUP_ID)).thenReturn(fakeGroup());
        when(groupService.isMember(GROUP_ID, user.getId())).thenReturn(true);

        com.letsroast.model.ChatMessage msg = new com.letsroast.model.ChatMessage(GROUP_ID, user.getId(), "Hello!");
        when(chatMessageService.postMessage(anyString(), anyString(), anyString())).thenReturn(msg);

        mockMvc.perform(post("/api/groups/" + GROUP_ID + "/messages")
                        .cookie(SESSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatMessageController.PostMessageRequest("Hello!"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Hello!"));
    }

    @Test
    void postMessage_unauthenticated_returns401() throws Exception {
        mockMvc.perform(post("/api/groups/" + GROUP_ID + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatMessageController.PostMessageRequest("Hi"))))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void postMessage_groupNotFound_returns404() throws Exception {
        User user = fakeUser();
        setupAuthenticatedUser(user);
        when(groupService.getGroupById(GROUP_ID)).thenReturn(null);

        mockMvc.perform(post("/api/groups/" + GROUP_ID + "/messages")
                        .cookie(SESSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatMessageController.PostMessageRequest("Hi"))))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("group not found"));
    }

    @Test
    void postMessage_notGroupMember_returns403() throws Exception {
        User user = fakeUser();
        setupAuthenticatedUser(user);
        when(groupService.getGroupById(GROUP_ID)).thenReturn(fakeGroup());
        when(groupService.isMember(GROUP_ID, user.getId())).thenReturn(false);

        mockMvc.perform(post("/api/groups/" + GROUP_ID + "/messages")
                        .cookie(SESSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatMessageController.PostMessageRequest("Hi"))))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error").value("user is not a group member"));
    }

    @Test
    void postMessage_blankMessage_returns400() throws Exception {
        User user = fakeUser();
        setupAuthenticatedUser(user);
        when(groupService.getGroupById(GROUP_ID)).thenReturn(fakeGroup());
        when(groupService.isMember(GROUP_ID, user.getId())).thenReturn(true);

        mockMvc.perform(post("/api/groups/" + GROUP_ID + "/messages")
                        .cookie(SESSION)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new ChatMessageController.PostMessageRequest("   "))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("message is required"));
    }

    // ── Get Messages ──────────────────────────────────────────────────────────

    @Test
    void listMessages_memberOfGroup_returnsMessages() throws Exception {
        User user = fakeUser();
        setupAuthenticatedUser(user);
        when(groupService.getGroupById(GROUP_ID)).thenReturn(fakeGroup());
        when(groupService.isMember(GROUP_ID, user.getId())).thenReturn(true);

        ChatMessageDTO dto = new ChatMessageDTO("msg-1", user.getId(), "alice", "Hey there", Instant.now());
        when(chatMessageService.getMessagesByGroupId(GROUP_ID)).thenReturn(List.of(dto));

        mockMvc.perform(get("/api/groups/" + GROUP_ID + "/messages").cookie(SESSION))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].message").value("Hey there"))
                .andExpect(jsonPath("$[0].username").value("alice"));
    }

    @Test
    void listMessages_notGroupMember_returns403() throws Exception {
        User user = fakeUser();
        setupAuthenticatedUser(user);
        when(groupService.getGroupById(GROUP_ID)).thenReturn(fakeGroup());
        when(groupService.isMember(GROUP_ID, user.getId())).thenReturn(false);

        mockMvc.perform(get("/api/groups/" + GROUP_ID + "/messages").cookie(SESSION))
                .andExpect(status().isForbidden());
    }

    @Test
    void listMessages_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/groups/" + GROUP_ID + "/messages"))
                .andExpect(status().isUnauthorized());
    }
}

