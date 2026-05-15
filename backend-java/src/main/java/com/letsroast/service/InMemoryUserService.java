package com.letsroast.service;

import com.letsroast.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile("default")
public class InMemoryUserService implements UserService {
    private final Map<String, User> users = new HashMap<>();
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public User registerUser(String username, String password) {
        User existingUser = getUserByUsername(username);
        if (existingUser != null) {
            return null;
        }
        User user = new User(username, passwordEncoder.encode(password));
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(String userId) {
        return users.get(userId);
    }

    @Override
    public User getUserByUsername(String username) {
        return users.values().stream()
                .filter(user -> user.getUsername().equalsIgnoreCase(username))
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean verifyPassword(User user, String password) {
        if (user == null || user.getPasswordHash() == null || password == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
}

