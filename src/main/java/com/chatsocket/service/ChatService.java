package com.chatsocket.service;

import com.chatsocket.model.ChatMessage;
import com.chatsocket.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        chatMessageRepository.save(message);
        return message;
    }
}

