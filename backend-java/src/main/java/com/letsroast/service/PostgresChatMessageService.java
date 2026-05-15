package com.letsroast.service;

import com.letsroast.api.ChatMessageDTO;
import com.letsroast.model.ChatMessage;
import com.letsroast.model.User;
import com.letsroast.repository.ChatMessageRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Profile("postgres")
public class PostgresChatMessageService implements ChatMessageService {
    private final ChatMessageRepository chatMessageRepository;
    private final UserService userService;

    public PostgresChatMessageService(ChatMessageRepository chatMessageRepository, UserService userService) {
        this.chatMessageRepository = chatMessageRepository;
        this.userService = userService;
    }

    @Override
    public ChatMessage postMessage(String groupId, String userId, String message) {
        ChatMessage chatMessage = new ChatMessage(groupId, userId, message);
        return chatMessageRepository.save(chatMessage);
    }

    @Override
    public List<ChatMessageDTO> getMessagesByGroupId(String groupId) {
        List<ChatMessage> messages = chatMessageRepository.findByGroupId(groupId);
        return messages.stream()
            .map(msg -> {
                User user = userService.getUserById(msg.getUserId());
                String username = user != null ? user.getUsername() : "Unknown";
                return new ChatMessageDTO(
                    msg.getId(),
                    msg.getUserId(),
                    username,
                    msg.getMessage(),
                    msg.getCreatedAt()
                );
            })
            .collect(Collectors.toList());
    }
}
