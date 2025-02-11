package com.chatsocket.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PrivateChatHandler extends TextWebSocketHandler {
    private final Map<String, WebSocketSession> userSessions = new HashMap<>();

    public PrivateChatHandler() {
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = getUsernameFromSession(session);
        userSessions.put(username, session);
        session.sendMessage(new TextMessage("Connected as: " + username));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        String sender = getUsernameFromSession(session);

        if (payload.startsWith("@")) {
            String[] parts = payload.split(" ", 2);
            if (parts.length > 1) {
                String recipient = parts[0].substring(1);
                sendPrivateMessage(sender, recipient, parts[1]);
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String username = getUsernameFromSession(session);
        userSessions.remove(username);
    }

    private void sendPrivateMessage(String sender, String recipient, String message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMessage = objectMapper.writeValueAsString(Map.of(
                "type", "private",
                "sender", sender,
                "receiver", recipient,
                "message", message
        ));

        WebSocketSession recipientSession = userSessions.get(recipient);
        WebSocketSession senderSession = userSessions.get(sender);

        if (recipientSession != null && recipientSession.isOpen()) {
            recipientSession.sendMessage(new TextMessage(jsonMessage));
        }

        if (senderSession != null && senderSession.isOpen()) {
            senderSession.sendMessage(new TextMessage(jsonMessage));
        }
    }

    private String getUsernameFromSession(WebSocketSession session) {
        List<String> usernameHeaders = session.getHandshakeHeaders().get("username");
        return (usernameHeaders != null && !usernameHeaders.isEmpty()) ? usernameHeaders.get(0) : "UnknownUser";
    }


}
