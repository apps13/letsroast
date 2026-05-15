package com.letsroast.service;

import com.letsroast.model.User;

public interface UserService {
    User registerUser(String username, String password);
    User getUserById(String userId);
    User getUserByUsername(String username);
    boolean verifyPassword(User user, String password);
}

