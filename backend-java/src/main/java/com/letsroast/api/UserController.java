package com.letsroast.api;

import com.letsroast.model.User;
import com.letsroast.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * API controller for user-related operations.
 *
 * <p>All routes in this class are under /api/users.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    // Constructor
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Creates a new user.
     *
     * @param request request body containing a username
     * @return created user or 400 when username is missing/blank
     */
    @PostMapping
    public ResponseEntity<?> createUser(@RequestBody CreateUserRequest request) {
        if (request == null || request.username() == null || request.username().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "username is required"));
        }

        User user = userService.registerUser(request.username().trim());
        return ResponseEntity.ok(user);
    }

    /**
     * Fetches a user by id.
     *
     * @param userId user identifier from the URL path
     * @return user payload or 404 when the user does not exist
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable String userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            return ResponseEntity.status(404).body(Map.of("error", "user not found"));
        }
        return ResponseEntity.ok(user);
    }

    /**
     * Request body for creating a user.
     *
     * @param username display name for the new user
     */
    public record CreateUserRequest(String username) {
    }
}

