package com.shivseshivam.realtimechatandteamcollaboration.controller;

import com.shivseshivam.realtimechatandteamcollaboration.model.ChatMessage;
import com.shivseshivam.realtimechatandteamcollaboration.service.MessageService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

//@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Controller
public class MessageController {

    private final MessageService messageService ;
    private final SimpMessagingTemplate simpMessagingTemplate;

    public MessageController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
    }

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(ChatMessage message, @DestinationVariable String roomId){
        message.setRoomId(roomId);
        message.setTimestamp(LocalDateTime.now());

        messageService.save(message);

        simpMessagingTemplate.convertAndSend("/topic/"+roomId,message);

    }
}
