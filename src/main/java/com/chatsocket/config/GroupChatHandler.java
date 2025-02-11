package com.chatsocket.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.*;

public class GroupChatHandler extends TextWebSocketHandler {
    private final Map<String, Set<WebSocketSession>> groupSessions = new HashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String groupName = getGroupFromSession(session);
        groupSessions.computeIfAbsent(groupName, k -> new HashSet<>()).add(session);
        session.sendMessage(new TextMessage("Joined group: " + groupName));
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(message.getPayload());

        String groupName = jsonNode.get("group").asText();
        String sender = jsonNode.get("sender").asText();
        String content = jsonNode.get("message").asText();

        String jsonResponse = objectMapper.writeValueAsString(Map.of(
                "type", "group",
                "group", groupName,
                "sender", sender,
                "message", content
        ));

        for (WebSocketSession s : groupSessions.getOrDefault(groupName, new HashSet<>())) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(jsonResponse));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String groupName = getGroupFromSession(session);
        groupSessions.getOrDefault(groupName, new HashSet<>()).remove(session);
    }

    private String getGroupFromSession(WebSocketSession session) {
        List<String> groupHeaders = session.getHandshakeHeaders().get("group");
        return (groupHeaders != null && !groupHeaders.isEmpty()) ? groupHeaders.get(0) : "general";
    }

}

