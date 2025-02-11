package com.chatsocket.controller;

import com.chatsocket.model.ChatMessage;
import com.chatsocket.repository.ChatMessageRepository;
import com.chatsocket.service.ChatService;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatService chatService;

    public ChatController(ChatMessageRepository chatMessageRepository, ChatService chatService) {
        this.chatMessageRepository = chatMessageRepository;
        this.chatService = chatService;
    }
    /*@MessageMapping("/send")
    @SendTo("/topic/messages")
    public ChatMessage sendMessage(ChatMessage message) {
        return message;
    }*/

    @GetMapping("/{user}")
    public List<ChatMessage> getUserChats(@PathVariable String user) {
        return chatMessageRepository.findByReceiverOrSender(user, user);
    }

}
