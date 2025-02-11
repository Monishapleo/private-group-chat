package com.chatsocket.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GroupChatHandler extends TextWebSocketHandler {
    private static final Map<String, Set<WebSocketSession>> groupSessions = new ConcurrentHashMap<>();
    public GroupChatHandler() {
        System.out.println(" GroupChatHandler Instance Created - Singleton Ensured");

    }
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String groupName = getEventFromSession(session);

        groupSessions.computeIfAbsent(groupName, k -> Collections.synchronizedSet(new HashSet<>())).add(session);

        System.out.println(" New user joined [" + groupName + "], Total: " + groupSessions.get(groupName).size());
        for (WebSocketSession s : groupSessions.get(groupName)) {
            System.out.println("    Active Session ID: " + s.getId());
        }

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

        Set<WebSocketSession> groupUsers = groupSessions.getOrDefault(groupName, new HashSet<>());

        System.out.println(" Sending Message to Group [" + groupName + "] - Users: " + groupUsers.size());
        for (WebSocketSession s : groupUsers) {
            System.out.println("   Active WebSocket ID: " + s.getId());
        }

        for (WebSocketSession s : groupUsers) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(jsonResponse));
                System.out.println("📩 Sent message to: " + s.getId());
            } else {
                System.out.println("Skipping closed session: " + s.getId());
            }
        }
    }



    public void sendMessageFromRest(String groupName, String sender, String message) throws Exception {
        sendMessageToGroup(groupName, sender, message);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        String groupName = getEventFromSession(session);

        Set<WebSocketSession> groupUsers = groupSessions.getOrDefault(groupName, Collections.synchronizedSet(new HashSet<>()));
        groupUsers.remove(session);

        System.out.println(" User left [" + groupName + "], Remaining: " + groupUsers.size());

        System.out.println(" Debugging Active Users After Disconnect:");
        groupSessions.forEach((key, sessions) -> {
            System.out.println("   Group: " + key + " | Users: " + sessions.size());
        });
    }


    private String getEventFromSession(WebSocketSession session) {
        List<String> groupHeaders = session.getHandshakeHeaders().get("event");
        String groupName = (groupHeaders != null && !groupHeaders.isEmpty()) ? groupHeaders.get(0).toLowerCase() : "general";
        System.out.println(" Extracted Group from Headers: " + groupName);
        return groupName;
    }

    public void sendMessageToGroup(String groupName, String sender, String message) throws Exception {
        groupName = groupName.toLowerCase();

        System.out.println(" Debugging Stored Sessions Before Sending:");
        groupSessions.forEach((key, sessions) -> {
            System.out.println("   Group Key: '" + key + "' | Users: " + sessions.size());
        });

        Set<WebSocketSession> groupUsers = groupSessions.getOrDefault(groupName, Collections.synchronizedSet(new HashSet<>()));

        System.out.println(" Sending Message to Group [" + groupName + "] - Users: " + groupUsers.size());

        if (groupUsers.isEmpty()) {
            System.out.println(" No active WebSocket connections found for group: " + groupName);
        }

        for (WebSocketSession session : groupUsers) {
            if (session.isOpen()) {
                session.sendMessage(new TextMessage(message));
                System.out.println("Message Sent to User: " + session.getId());
            } else {
                System.out.println(" Skipping Closed Session: " + session.getId());
            }
        }
    }

}




