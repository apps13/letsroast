package com.letsroast.api;

import com.letsroast.model.Group;
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
    private final GroupService groupService;
    private final UserService userService;

    public GroupController(GroupService groupService, UserService userService) {
        this.groupService = groupService;
        this.userService = userService;
    }

    /**
     * Creates a new group.
     *
     * @param request request body containing group name and creator user id
     * @return created group, 400 for invalid input, or 404 when creator user is not found
     */
    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest request) {
        if (request == null || request.name() == null || request.name().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "group name is required"));
        }
        if (request.createdBy() == null || request.createdBy().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "createdBy userId is required"));
        }
        if (userService.getUserById(request.createdBy()) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "creator user not found"));
        }

        Group group = groupService.createGroup(request.name().trim(), request.createdBy());
        return ResponseEntity.ok(group);
    }

    /**
     * Lists all groups currently available.
     *
     * @return list of group objects
     */
    @GetMapping
    public List<Group> listGroups() {
        return groupService.listAllGroups();
    }

    /**
     * Adds an existing user to an existing group.
     *
     * @param groupId group identifier from the URL path
     * @param request request body containing the user id
     * @return join confirmation payload, 400 for invalid input, or 404 when group/user is missing
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<?> joinGroup(@PathVariable String groupId, @RequestBody JoinGroupRequest request) {
        if (groupService.getGroupById(groupId) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "group not found"));
        }
        if (request == null || request.userId() == null || request.userId().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId is required"));
        }
        if (userService.getUserById(request.userId()) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "user not found"));
        }

        groupService.joinGroup(groupId, request.userId());
        return ResponseEntity.ok(Map.of(
                "message", "joined group",
                "groupId", groupId,
                "userId", request.userId()
        ));
    }

    /**
     * Request body for creating a group.
     *
     * @param name group name
     * @param createdBy creator user id
     */
    public record CreateGroupRequest(String name, String createdBy) {
    }

    /**
     * Request body for joining a group.
     *
     * @param userId user id to add to the group
     */
    public record JoinGroupRequest(String userId) {
    }
}

