package com.shivseshivam.realtimechatandteamcollaboration.repository;

import com.shivseshivam.realtimechatandteamcollaboration.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Long> {
}
