package com.letsroast.service;

import com.letsroast.api.ChatMessageDTO;
import com.letsroast.model.ChatMessage;
import com.letsroast.model.User;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Profile("default")
public class InMemoryChatMessageService implements ChatMessageService {
    private final List<ChatMessage> messages = new ArrayList<>();
    private final UserService userService;

    public InMemoryChatMessageService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public ChatMessage postMessage(String groupId, String userId, String message) {
        ChatMessage chatMessage = new ChatMessage(groupId, userId, message);
        messages.add(chatMessage);
        return chatMessage;
    }

    @Override
    public List<ChatMessageDTO> getMessagesByGroupId(String groupId) {
        List<ChatMessageDTO> filtered = new ArrayList<>();
        for (ChatMessage msg : messages) {
            if (msg.getGroupId().equals(groupId)) {
                User user = userService.getUserById(msg.getUserId());
                String username = user != null ? user.getUsername() : "Unknown";
                filtered.add(new ChatMessageDTO(
                    msg.getId(),
                    msg.getUserId(),
                    username,
                    msg.getMessage(),
                    msg.getCreatedAt()
                ));
            }
        }
        return filtered;
    }
}

