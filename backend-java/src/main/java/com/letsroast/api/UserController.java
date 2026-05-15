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

}

