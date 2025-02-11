package com.chatsocket.service;

import com.chatsocket.model.ChatMessage;
import com.chatsocket.repository.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public List<ChatMessage> getChatBetweenUsers(String user1, String user2) {
        return chatMessageRepository.findByReceiverOrSender(user1, user2);
    }
}
