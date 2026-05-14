package com.letsroast.api;

import com.letsroast.model.ChatMessage;
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
    private final ChatMessageService chatMessageService;
    private final GroupService groupService;
    private final UserService userService;

    public ChatMessageController(
            ChatMessageService chatMessageService,
            GroupService groupService,
            UserService userService
    ) {
        this.chatMessageService = chatMessageService;
        this.groupService = groupService;
        this.userService = userService;
    }

    /**
     * Posts a new message to a group.
     *
     * @param groupId target group id from the URL path
     * @param request request body with user id and message text
     * @return created message, or 400/403/404 for validation and access failures
     */
    @PostMapping
    public ResponseEntity<?> postMessage(@PathVariable String groupId, @RequestBody PostMessageRequest request) {
        if (groupService.getGroupById(groupId) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "group not found"));
        }
        if (request == null || request.userId() == null || request.userId().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId is required"));
        }
        if (request.message() == null || request.message().isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "message is required"));
        }
        if (userService.getUserById(request.userId()) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "user not found"));
        }
        if (!groupService.isMember(groupId, request.userId())) {
            return ResponseEntity.status(403).body(Map.of("error", "user is not a group member"));
        }

        ChatMessage chatMessage = chatMessageService.postMessage(groupId, request.userId(), request.message().trim());
        return ResponseEntity.ok(chatMessage);
    }

    /**
     * Returns all messages for a group for an existing group member.
     *
     * @param groupId target group id from the URL path
     * @param userId user id query parameter used for membership check
     * @return list of chat messages, or 400/403/404 when checks fail
     */
    @GetMapping
    public ResponseEntity<?> listMessages(@PathVariable String groupId, @RequestParam String userId) {
        if (groupService.getGroupById(groupId) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "group not found"));
        }
        if (userId == null || userId.isBlank()) {
            return ResponseEntity.badRequest().body(Map.of("error", "userId query parameter is required"));
        }
        if (userService.getUserById(userId) == null) {
            return ResponseEntity.status(404).body(Map.of("error", "user not found"));
        }
        if (!groupService.isMember(groupId, userId)) {
            return ResponseEntity.status(403).body(Map.of("error", "user is not a group member"));
        }

        List<ChatMessage> messages = chatMessageService.getMessagesByGroupId(groupId);
        return ResponseEntity.ok(messages);
    }

    /**
     * Request body for posting a new message.
     *
     * @param userId sender user id
     * @param message message text content
     */
    public record PostMessageRequest(String userId, String message) {
    }
}

