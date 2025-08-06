package com.zephyros.urbanup.controller;

import java.security.Principal;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;

import com.zephyros.urbanup.model.Message;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.service.RealtimeChatService;
import com.zephyros.urbanup.service.UserService;

/**
 * WebSocket Controller for real-time messaging
 */
@Controller
public class RealtimeWebSocketController {
    
    @Autowired
    private RealtimeChatService realtimeChatService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Handle WebSocket message sending
     */
    @MessageMapping("/chat/{chatId}/send")
    public void handleChatMessage(
            @DestinationVariable Long chatId,
            @Payload Map<String, Object> payload,
            Principal principal) {
        
        try {
            String email = principal.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return;
            }
            
            String content = (String) payload.get("content");
            String messageTypeStr = (String) payload.get("messageType");
            
            if (content == null || content.trim().isEmpty()) {
                return;
            }
            
            Message.MessageType messageType = Message.MessageType.TEXT;
            if (messageTypeStr != null) {
                try {
                    messageType = Message.MessageType.valueOf(messageTypeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    messageType = Message.MessageType.TEXT;
                }
            }
            
            realtimeChatService.sendRealtimeMessage(chatId, userOpt.get().getId(), content, messageType);
            
        } catch (Exception e) {
            System.err.println("Error handling chat message: " + e.getMessage());
        }
    }
    
    /**
     * Handle typing indicator
     */
    @MessageMapping("/chat/{chatId}/typing")
    public void handleTypingIndicator(
            @DestinationVariable Long chatId,
            @Payload Map<String, Object> payload,
            Principal principal) {
        
        try {
            String email = principal.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return;
            }
            
            Boolean isTyping = (Boolean) payload.get("isTyping");
            if (isTyping == null) {
                isTyping = false;
            }
            
            realtimeChatService.broadcastTypingIndicator(chatId, userOpt.get().getId(), isTyping);
            
        } catch (Exception e) {
            System.err.println("Error handling typing indicator: " + e.getMessage());
        }
    }
    
    /**
     * Handle user presence updates
     */
    @MessageMapping("/chat/{chatId}/presence")
    public void handlePresenceUpdate(
            @DestinationVariable Long chatId,
            @Payload Map<String, Object> payload,
            Principal principal) {
        
        try {
            String email = principal.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return;
            }
            
            String status = (String) payload.get("status");
            if (status == null) {
                status = "online";
            }
            
            realtimeChatService.broadcastUserPresence(chatId, userOpt.get().getId(), status);
            
        } catch (Exception e) {
            System.err.println("Error handling presence update: " + e.getMessage());
        }
    }
    
    /**
     * Handle read status updates
     */
    @MessageMapping("/chat/{chatId}/read")
    public void handleReadStatus(
            @DestinationVariable Long chatId,
            Principal principal) {
        
        try {
            String email = principal.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return;
            }
            
            realtimeChatService.markMessagesAsRead(chatId, userOpt.get().getId());
            
        } catch (Exception e) {
            System.err.println("Error handling read status: " + e.getMessage());
        }
    }
}
