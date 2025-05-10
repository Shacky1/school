package com.shacv.school.controller;

import com.shacv.school.entity.Message;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private List<Message> messages = new ArrayList<>();

    public ChatController(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @GetMapping("/chat")
    public String chat(Model model) {
        model.addAttribute("messages", messages); // Add messages to the model
        return "chat"; // Return the chat Thymeleaf template
    }

    @MessageMapping("/sendMessage")
    public void handleMessage(Message message) {
        message.setTimestamp(LocalDateTime.now()); // Set the timestamp for each message
        messages.add(message); // Add the message to the message history
        messagingTemplate.convertAndSend("/topic/messages", message); // Send the message to subscribers
    }
}
