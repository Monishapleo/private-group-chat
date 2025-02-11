package com.chatsocket.repository;

import com.chatsocket.model.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
    List<ChatMessage> findByReceiverOrSender(String receiver, String sender);
}


