package com.chatsocket.controller;

import com.chatsocket.config.GroupChatHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/chat")
public class ChatRestController {

    private final GroupChatHandler groupChatHandler;

    @Autowired
    public ChatRestController(GroupChatHandler groupChatHandler) {
        this.groupChatHandler = groupChatHandler;
    }

    @PostMapping("/send")
    public String sendMessage(@RequestBody Map<String, String> request) {
        String group = request.get("group");
        String sender = request.get("sender");
        String message = request.get("message");

        if (group == null || sender == null || message == null) {
            return "Missing required fields!";
        }

        try {
            groupChatHandler.sendMessageToGroup(group, sender, message);
            return "Message sent to group: " + group;
        } catch (Exception e) {
            return "Error sending message: " + e.getMessage();
        }
    }
}

