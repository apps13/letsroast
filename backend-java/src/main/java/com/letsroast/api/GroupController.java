package com.letsroast.api;

import com.letsroast.model.Group;
import com.letsroast.model.User;
import com.letsroast.service.AuthSessionService;
import com.letsroast.service.GroupService;
import com.letsroast.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API controller for group lifecycle operations.
 *
 * <p>All routes in this class are under /api/groups.
 */
@RestController
@RequestMapping("/api/groups")
public class GroupController {
    private static final String SESSION_COOKIE = "letsroast_session";

    private final GroupService groupService;
    private final UserService userService;
    private final AuthSessionService authSessionService;

    public GroupController(GroupService groupService, UserService userService, AuthSessionService authSessionService) {
        this.groupService = groupService;
        this.userService = userService;
        this.authSessionService = authSessionService;
    }

    /**
     * Creates a new group.
     *
     * @param request request body containing group name and creator user id
     * @return created group, 400 for invalid input, or 404 when creator user is not found
     */
    @PostMapping
    public ResponseEntity<?> createGroup(
            @CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
            @RequestBody CreateGroupRequest request
    ) {
        User currentUser = getAuthenticatedUser(sessionId);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "not authenticated"));
        }
        if (request == null || request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "group name is required"));
        }

        Group group = groupService.createGroup(request.name().trim(), currentUser.getId());
        return ResponseEntity.ok(group);
    }

    /**
     * Lists all groups currently available.
     *
     * @return list of group objects
     */
    @GetMapping
    public ResponseEntity<?> listGroups(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId) {
        User currentUser = getAuthenticatedUser(sessionId);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "not authenticated"));
        }
        return ResponseEntity.ok(groupService.listAllGroups());
    }

    @GetMapping("/mine")
    public ResponseEntity<?> listMyGroups(@CookieValue(name = SESSION_COOKIE, required = false) String sessionId) {
        User currentUser = getAuthenticatedUser(sessionId);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "not authenticated"));
        }
        return ResponseEntity.ok(groupService.listGroupsForUser(currentUser.getId()));
    }

    /**
     * Adds an existing user to an existing group.
     *
     * @param groupId group identifier from the URL path
     * @param request request body containing the user id
     * @return join confirmation payload, 400 for invalid input, or 404 when group/user is missing
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<?> joinGroup(
            @CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
            @PathVariable String groupId
    ) {
        User currentUser = getAuthenticatedUser(sessionId);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "not authenticated"));
        }
        if (groupService.getGroupById(groupId) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "group not found"));
        }

        groupService.joinGroup(groupId, currentUser.getId());
        return ResponseEntity.ok(Map.of(
                "message", "joined group",
                "groupId", groupId,
                "userId", currentUser.getId()
        ));
    }

    private User getAuthenticatedUser(String sessionId) {
        String userId = authSessionService.getUserIdFromSession(sessionId);
        if (userId == null) {
            return null;
        }
        return userService.getUserById(userId);
    }

    /**
     * Request body for creating a group.
     *
     * @param name group name
     */
    public record CreateGroupRequest(String name) {
    }
}

