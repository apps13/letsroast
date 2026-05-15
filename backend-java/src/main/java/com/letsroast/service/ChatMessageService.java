package com.letsroast.service;

import com.letsroast.api.ChatMessageDTO;
import com.letsroast.model.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    ChatMessage postMessage(String groupId, String userId, String message);
    List<ChatMessageDTO> getMessagesByGroupId(String groupId);
}

