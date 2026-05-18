package com.letsroast.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.letsroast.model.User;
import com.letsroast.service.AuthSessionService;
import com.letsroast.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    UserService userService;

    @MockBean
    AuthSessionService authSessionService;

    private User fakeUser(String username) {
        User u = new User(username, "hashed");
        return u;
    }

    // ── Register ─────────────────────────────────────────────────────────────

    @Test
    void register_validRequest_returns200WithUser() throws Exception {
        User user = fakeUser("alice");
        when(userService.registerUser("alice", "password123")).thenReturn(user);
        when(authSessionService.createSession(any())).thenReturn("session-abc");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthController.AuthRequest("alice", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(header().exists("Set-Cookie"));
    }

    @Test
    void register_duplicateUsername_returns409() throws Exception {
        when(userService.registerUser(anyString(), anyString())).thenReturn(null);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthController.AuthRequest("alice", "password123"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void register_missingUsername_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthController.AuthRequest("", "password123"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void register_shortPassword_returns400() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthController.AuthRequest("alice", "abc"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("password must be at least 6 characters"));
    }

    // ── Login ─────────────────────────────────────────────────────────────────

    @Test
    void login_validCredentials_returns200WithUser() throws Exception {
        User user = fakeUser("bob");
        when(userService.getUserByUsername("bob")).thenReturn(user);
        when(userService.verifyPassword(any(), anyString())).thenReturn(true);
        when(authSessionService.createSession(any())).thenReturn("session-xyz");

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthController.AuthRequest("bob", "password123"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("bob"));
    }

    @Test
    void login_wrongPassword_returns401() throws Exception {
        User user = fakeUser("bob");
        when(userService.getUserByUsername("bob")).thenReturn(user);
        when(userService.verifyPassword(any(), anyString())).thenReturn(false);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AuthController.AuthRequest("bob", "wrongpass"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── Me ────────────────────────────────────────────────────────────────────

    @Test
    void me_withValidSession_returnsUser() throws Exception {
        User user = fakeUser("carol");
        when(authSessionService.getUserIdFromSession("s1")).thenReturn("user-1");
        when(userService.getUserById("user-1")).thenReturn(user);

        mockMvc.perform(get("/api/auth/me").cookie(new jakarta.servlet.http.Cookie("letsroast_session", "s1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("carol"));
    }

    @Test
    void me_withNoSession_returns401() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").exists());
    }

    // ── Logout ────────────────────────────────────────────────────────────────

    @Test
    void logout_clearsSessionCookie() throws Exception {
        mockMvc.perform(post("/api/auth/logout")
                        .cookie(new jakarta.servlet.http.Cookie("letsroast_session", "s1")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("logged out"))
                .andExpect(header().exists("Set-Cookie"));
    }
}

