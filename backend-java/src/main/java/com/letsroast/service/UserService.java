package com.letsroast.service;

import com.letsroast.model.User;

public interface UserService {
    User registerUser(String username);
    User getUserById(String userId);
    User getUserByUsername(String username);
}

