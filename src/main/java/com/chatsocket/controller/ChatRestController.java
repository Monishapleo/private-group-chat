package com.chatsocket.controller;

import com.chatsocket.model.ChatMessage;
import com.chatsocket.service.ChatService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final ChatService chatService;

    public ChatRestController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/send")
    public ResponseEntity<ChatMessage> sendMessage(@RequestBody ChatMessage message) {
        ChatMessage savedMessage = chatService.saveMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/{user1}/{user2}")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@PathVariable String user1, @PathVariable String user2) {
        List<ChatMessage> chatHistory = chatService.getChatBetweenUsers(user1, user2);
        return ResponseEntity.ok(chatHistory);
    }
}
