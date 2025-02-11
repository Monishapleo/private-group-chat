package com.chatsocket.config;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ChatWebSocketHandler extends TextWebSocketHandler {

    private static final Map<String, WebSocketSession> userSessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = getUsername(session);
        if (username != null) {
            userSessions.put(username, session);
            System.out.println(username + " connected! Current users: " + userSessions.keySet());
        } else {
            session.close(CloseStatus.BAD_DATA);
            System.out.println("Connection rejected: No username provided.");
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        System.out.println("Received message: " + payload);

        // Parse JSON manually (for simplicity)
        String[] parts = payload.replace("\"", "").replace("{", "").replace("}", "").split(",");
        String sender = parts[0].split(":")[1].trim();
        String receiver = parts[1].split(":")[1].trim(); // Receiver username
        String content = parts[2].split(":")[1].trim();

        String response = "{\"sender\":\"" + sender + "\",\"content\":\"" + content + "\"}";

        // Find receiver session
        WebSocketSession receiverSession = userSessions.get(receiver);

        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(response));
            System.out.println("Message sent to " + receiver);
        } else {
            System.out.println("User not found or not connected: " + receiver);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = getUsername(session);
        if (username != null) {
            userSessions.remove(username);
            System.out.println(username + " disconnected. Remaining users: " + userSessions.keySet());
        }
    }

    private String getUsername(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query != null && query.startsWith("username=")) {
            return query.split("=")[1];
        }
        return null;
    }
}
