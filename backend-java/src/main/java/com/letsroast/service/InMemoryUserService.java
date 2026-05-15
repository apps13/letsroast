package com.letsroast.service;

import com.letsroast.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Profile("default")
public class InMemoryUserService implements UserService {
    private final Map<String, User> users = new HashMap<>();

    @Override
    public User registerUser(String username) {
        User user = new User(username);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User getUserById(String userId) {
        return users.get(userId);
    }
}

