package com.letsroast.service;

import com.letsroast.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Service
@Profile("postgres")
public class PostgresUserService implements UserService {

    @Override
    public User registerUser(String username) {
        throw new UnsupportedOperationException("PostgresUserService is not implemented yet.");
    }

    @Override
    public User getUserById(String userId) {
        throw new UnsupportedOperationException("PostgresUserService is not implemented yet.");
    }
}

