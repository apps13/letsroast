package com.letsroast.api;

import com.letsroast.model.ChatMessage;
import com.letsroast.model.User;
import com.letsroast.service.AuthSessionService;
import com.letsroast.service.ChatMessageService;
import com.letsroast.service.GroupService;
import com.letsroast.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * API controller for reading and posting chat messages inside a group.
 *
 * <p>Routes in this class are under /api/groups/{groupId}/messages.
 */
@RestController
@RequestMapping("/api/groups/{groupId}/messages")
public class ChatMessageController {
    private static final String SESSION_COOKIE = "letsroast_session";

    private final ChatMessageService chatMessageService;
    private final GroupService groupService;
    private final UserService userService;
    private final AuthSessionService authSessionService;

    public ChatMessageController(
            ChatMessageService chatMessageService,
            GroupService groupService,
            UserService userService,
            AuthSessionService authSessionService
    ) {
        this.chatMessageService = chatMessageService;
        this.groupService = groupService;
        this.userService = userService;
        this.authSessionService = authSessionService;
    }

    /**
     * Posts a new message to a group.
     *
     * @param groupId target group id from the URL path
     * @param request request body with user id and message text
     * @return created message, or 400/403/404 for validation and access failures
     */
    @PostMapping
    public ResponseEntity<?> postMessage(
            @CookieValue(name = SESSION_COOKIE, required = false) String sessionId,
            @PathVariable String groupId,
            @RequestBody PostMessageRequest request
    ) {
        User currentUser = getAuthenticatedUser(sessionId);
        if (currentUser == null) {
            return ResponseEntity.status(401).body(Map.of("error", "not authenticated"));
        }
        if (groupService.getGroupById(groupId) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "group not found"));
        }
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "message is required"));
        }
        if (!groupService.isMember(groupId, currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "user is not a group member"));
        }

        ChatMessage chatMessage = chatMessageService.postMessage(groupId, currentUser.getId(), request.message().trim());
        return ResponseEntity.ok(chatMessage);
    }

    /**
     * Returns all messages for a group for an existing group member.
     *
     * @param groupId target group id from the URL path
     * @param userId user id query parameter used for membership check
     * @return list of chat messages with usernames, or 400/403/404 when checks fail
     */
    @GetMapping
    public ResponseEntity<?> listMessages(
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
        if (!groupService.isMember(groupId, currentUser.getId())) {
            return ResponseEntity.status(403).body(Map.of("error", "user is not a group member"));
        }

        List<ChatMessageDTO> messages = chatMessageService.getMessagesByGroupId(groupId);
        return ResponseEntity.ok(messages);
    }

    private User getAuthenticatedUser(String sessionId) {
        String userId = authSessionService.getUserIdFromSession(sessionId);
        if (userId == null) {
            return null;
        }
        return userService.getUserById(userId);
    }

    /**
     * Request body for posting a new message.
     *
     * @param message message text content
     */
    public record PostMessageRequest(String message) {
    }
}

