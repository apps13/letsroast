package com.letsroast.service;

import com.letsroast.model.User;
import com.letsroast.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Profile("postgres")
public class PostgresUserService implements UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public PostgresUserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User registerUser(String username, String password) {
        User existingUser = userRepository.findByUsernameIgnoreCase(username).orElse(null);
        if (existingUser != null) {
            return null;
        }
        User user = new User(username, passwordEncoder.encode(password));
        return userRepository.save(user);
    }

    @Override
    public User getUserById(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsernameIgnoreCase(username).orElse(null);
    }

    @Override
    public boolean verifyPassword(User user, String password) {
        if (user == null || user.getPasswordHash() == null || password == null) {
            return false;
        }
        return passwordEncoder.matches(password, user.getPasswordHash());
    }
}

