package com.letsroast.service;

import com.letsroast.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryUserServiceTest {

    private InMemoryUserService userService;

    @BeforeEach
    void setUp() {
        userService = new InMemoryUserService();
    }

    @Test
    void registerUser_returnsUserWithCorrectUsername() {
        User user = userService.registerUser("alice", "password123");

        assertThat(user).isNotNull();
        assertThat(user.getUsername()).isEqualTo("alice");
        assertThat(user.getId()).isNotBlank();
    }

    @Test
    void registerUser_storesHashedPasswordNotPlaintext() {
        User user = userService.registerUser("bob", "secret99");

        assertThat(user.getPasswordHash()).isNotEqualTo("secret99");
        assertThat(user.getPasswordHash()).isNotBlank();
    }

    @Test
    void registerUser_duplicateUsername_returnsNull() {
        userService.registerUser("alice", "password123");
        User duplicate = userService.registerUser("alice", "anotherpass");

        assertThat(duplicate).isNull();
    }

    @Test
    void registerUser_duplicateUsername_isCaseInsensitive() {
        userService.registerUser("alice", "password123");
        User duplicate = userService.registerUser("ALICE", "anotherpass");

        assertThat(duplicate).isNull();
    }

    @Test
    void getUserById_returnsCorrectUser() {
        User registered = userService.registerUser("carol", "password123");

        User found = userService.getUserById(registered.getId());

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("carol");
    }

    @Test
    void getUserById_unknownId_returnsNull() {
        assertThat(userService.getUserById("nonexistent-id")).isNull();
    }

    @Test
    void getUserByUsername_returnsCorrectUser() {
        userService.registerUser("dave", "password123");

        User found = userService.getUserByUsername("dave");

        assertThat(found).isNotNull();
        assertThat(found.getUsername()).isEqualTo("dave");
    }

    @Test
    void getUserByUsername_unknownUsername_returnsNull() {
        assertThat(userService.getUserByUsername("nobody")).isNull();
    }

    @Test
    void verifyPassword_correctPassword_returnsTrue() {
        User user = userService.registerUser("eve", "mypassword");

        assertThat(userService.verifyPassword(user, "mypassword")).isTrue();
    }

    @Test
    void verifyPassword_wrongPassword_returnsFalse() {
        User user = userService.registerUser("frank", "correctpass");

        assertThat(userService.verifyPassword(user, "wrongpass")).isFalse();
    }

    @Test
    void verifyPassword_nullUser_returnsFalse() {
        assertThat(userService.verifyPassword(null, "anypass")).isFalse();
    }
}

