package com.letsroast.repository;

import com.letsroast.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
	Optional<User> findByUsernameIgnoreCase(String username);
}

