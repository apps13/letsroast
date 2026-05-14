package com.letsroast.service;

import com.letsroast.model.ChatMessage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Profile("postgres")
public class PostgresChatMessageService implements ChatMessageService {

    @Override
    public ChatMessage postMessage(String groupId, String userId, String message) {
        throw new UnsupportedOperationException("PostgresChatMessageService is not implemented yet.");
    }

    @Override
    public List<ChatMessage> getMessagesByGroupId(String groupId) {
        throw new UnsupportedOperationException("PostgresChatMessageService is not implemented yet.");
    }
}
