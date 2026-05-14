package com.letsroast.service;

import com.letsroast.model.ChatMessage;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class InMemoryChatMessageService implements ChatMessageService {
    private final List<ChatMessage> messages = new ArrayList<>();

    @Override
    public ChatMessage postMessage(String groupId, String userId, String message) {
        ChatMessage chatMessage = new ChatMessage(groupId, userId, message);
        messages.add(chatMessage);
        return chatMessage;
    }

    @Override
    public List<ChatMessage> getMessagesByGroupId(String groupId) {
        List<ChatMessage> filtered = new ArrayList<>();
        for (ChatMessage msg : messages) {
            if (msg.getGroupId().equals(groupId)) {
                filtered.add(msg);
            }
        }
        return filtered;
    }
}

