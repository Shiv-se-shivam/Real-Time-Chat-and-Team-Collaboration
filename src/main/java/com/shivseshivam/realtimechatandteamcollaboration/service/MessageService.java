package com.shivseshivam.realtimechatandteamcollaboration.service;

import com.shivseshivam.realtimechatandteamcollaboration.model.ChatMessage;
import com.shivseshivam.realtimechatandteamcollaboration.repository.MessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class MessageService {

    @AutoConfigureOrder
    private final MessageRepository messageRepository;

    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public void save(ChatMessage chatMessage) {
        messageRepository.save(chatMessage);
    }
}
