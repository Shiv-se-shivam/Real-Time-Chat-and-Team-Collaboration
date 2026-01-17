package com.shivseshivam.realtimechatandteamcollaboration.controller;

import com.shivseshivam.realtimechatandteamcollaboration.model.ChatMessage;
import com.shivseshivam.realtimechatandteamcollaboration.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.time.LocalDateTime;

//@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Controller
@Slf4j
public class MessageController {

    private final MessageService messageService;
    private final SimpMessagingTemplate simpMessagingTemplate;
    private final ChatClient chatClient;

    public MessageController(MessageService messageService, SimpMessagingTemplate simpMessagingTemplate, ChatClient.Builder chatClientBuilder) {
        this.messageService = messageService;
        this.simpMessagingTemplate = simpMessagingTemplate;
        this.chatClient = chatClientBuilder
                .defaultSystem("I'm a helpful Chat Assistant !!")
                .build();
    }

    @MessageMapping("/chat/{roomId}")
    public void sendMessage(@Payload ChatMessage message, @DestinationVariable String roomId) {
        message.setRoomId(roomId);
        message.setTimestamp(LocalDateTime.now());

        messageService.save(message);

        simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, message);

        if (message.getContent().toLowerCase().startsWith("@ai")) {
            processAiRequest(roomId, message.getContent().replace("@ai", "").trim());
        }

    }

    private void processAiRequest(String roomId, String prompt) {
        try {
            log.info("AiRequest for " + roomId + ": " + prompt);
            String aiResponseText = chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();

            ChatMessage aiMessage = new ChatMessage();
            aiMessage.setContent(aiResponseText);
            aiMessage.setRoomId(roomId);
            aiMessage.setTimestamp(LocalDateTime.now());
            aiMessage.setSender("AI Assistant");

            simpMessagingTemplate.convertAndSend("/topic/room/" + roomId, aiMessage);
        } catch (Exception e) {
            log.error("AI Error message {}",e.getMessage());
            simpMessagingTemplate.convertAndSend("/topic/room/" + roomId,
                    new ChatMessage(null,"System","AI Error :"+e.getMessage(),roomId,LocalDateTime.now()));
        }
    }
}
