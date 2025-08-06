package com.zephyros.urbanup.controller;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.zephyros.urbanup.dto.ApiResponse;
import com.zephyros.urbanup.model.Chat;
import com.zephyros.urbanup.model.Message;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.service.RealtimeChatService;
import com.zephyros.urbanup.service.UserService;

@RestController
@RequestMapping("/realtime-chat")
public class RealtimeChatController {
    
    @Autowired
    private RealtimeChatService realtimeChatService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create or get chat for a task
     */
    @PostMapping("/create/{taskId}")
    public ResponseEntity<ApiResponse<Chat>> createOrGetChat(
            @PathVariable Long taskId,
            @RequestParam Long fulfillerId,
            Authentication authentication) {
        
        try {
            Chat chat = realtimeChatService.createOrGetChat(taskId, fulfillerId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Chat created/retrieved successfully", chat));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Failed to create/get chat", null));
        }
    }
    
    /**
     * Get user's chats
     */
    @GetMapping("/my-chats")
    public ResponseEntity<ApiResponse<List<Chat>>> getUserChats(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "User not found", null));
            }
            
            List<Chat> chats = realtimeChatService.getUserChats(userOpt.get().getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Chats retrieved successfully", chats));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Failed to retrieve chats", null));
        }
    }
    
    /**
     * Get chat messages
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<List<Message>>> getChatMessages(
            @PathVariable Long chatId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size,
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "User not found", null));
            }
            
            List<Message> messages = realtimeChatService.getChatMessages(chatId, userOpt.get().getId(), page, size);
            return ResponseEntity.ok(new ApiResponse<>(true, "Messages retrieved successfully", messages));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Failed to retrieve messages", null));
        }
    }
    
    /**
     * Send text message
     */
    @PostMapping("/{chatId}/send")
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            @PathVariable Long chatId,
            @RequestBody Map<String, String> payload,
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "User not found", null));
            }
            
            String content = payload.get("content");
            if (content == null || content.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "Message content cannot be empty", null));
            }
            
            Message message = realtimeChatService.sendRealtimeMessage(
                chatId, userOpt.get().getId(), content, Message.MessageType.TEXT);
            
            return ResponseEntity.ok(new ApiResponse<>(true, "Message sent successfully", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Failed to send message", null));
        }
    }
    
    /**
     * Send media message
     */
    @PostMapping("/{chatId}/send-media")
    public ResponseEntity<ApiResponse<Message>> sendMediaMessage(
            @PathVariable Long chatId,
            @RequestPart("file") MultipartFile file,
            @RequestPart(value = "caption", required = false) String caption,
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "User not found", null));
            }
            
            Message message = realtimeChatService.sendMediaMessage(chatId, userOpt.get().getId(), file, caption);
            return ResponseEntity.ok(new ApiResponse<>(true, "Media message sent successfully", message));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Failed to send media message", null));
        }
    }
    
    /**
     * Mark messages as read
     */
    @PostMapping("/{chatId}/mark-read")
    public ResponseEntity<ApiResponse<Void>> markMessagesAsRead(
            @PathVariable Long chatId,
            Authentication authentication) {
        
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "User not found", null));
            }
            
            realtimeChatService.markMessagesAsRead(chatId, userOpt.get().getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Messages marked as read", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                .body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Failed to mark messages as read", null));
        }
    }
    
    /**
     * Get unread message count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<User> userOpt = userService.getUserByEmail(email);
            if (userOpt.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ApiResponse<>(false, "User not found", null));
            }
            
            long unreadCount = realtimeChatService.getUnreadMessageCount(userOpt.get().getId());
            return ResponseEntity.ok(new ApiResponse<>(true, "Unread count retrieved successfully", unreadCount));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                .body(new ApiResponse<>(false, "Failed to retrieve unread count", null));
        }
    }
}
