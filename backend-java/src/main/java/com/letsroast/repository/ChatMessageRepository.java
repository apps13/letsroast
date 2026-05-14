package com.letsroast.repository;

import com.letsroast.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, String> {
    @Query("SELECT m FROM ChatMessage m WHERE m.groupId = :groupId ORDER BY m.createdAt ASC")
    List<ChatMessage> findByGroupId(@Param("groupId") String groupId);
}

