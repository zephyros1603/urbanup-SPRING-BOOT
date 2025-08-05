package com.zephyros.urbanup.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zephyros.urbanup.dto.ApiResponse;
import com.zephyros.urbanup.dto.ChatCreateDto;
import com.zephyros.urbanup.dto.MessageSendDto;
import com.zephyros.urbanup.model.Chat;
import com.zephyros.urbanup.model.Message;
import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.service.ChatService;
import com.zephyros.urbanup.service.TaskService;
import com.zephyros.urbanup.service.UserService;

@RestController
@RequestMapping("/chats")
public class ChatController {
    
    @Autowired
    private ChatService chatService;
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private UserService userService;
    
    /**
     * Create or get a chat for a task
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Chat>> createOrGetChat(@RequestBody ChatCreateDto chatDto) {
        try {
            // Get Task and User objects
            Optional<Task> taskOpt = taskService.getTaskById(chatDto.getTaskId());
            Optional<User> userOpt = userService.getUserById(chatDto.getFulfillerId());
            
            if (taskOpt.isEmpty()) {
                ApiResponse<Chat> response = new ApiResponse<>(false, "Task not found", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            if (userOpt.isEmpty()) {
                ApiResponse<Chat> response = new ApiResponse<>(false, "User not found", null);
                return ResponseEntity.badRequest().body(response);
            }
            
            Chat chat = chatService.getOrCreateTaskChat(taskOpt.get(), userOpt.get());
            
            ApiResponse<Chat> response = new ApiResponse<>(true, "Chat created/retrieved successfully", chat);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<Chat> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<Chat> response = new ApiResponse<>(false, "Failed to create/get chat", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get chat by ID
     */
    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<Chat>> getChat(
            @PathVariable Long chatId,
            @RequestParam Long userId) {
        try {
            Optional<Chat> chatOpt = chatService.getChatById(chatId, userId);
            
            if (chatOpt.isPresent()) {
                ApiResponse<Chat> response = new ApiResponse<>(true, "Chat found", chatOpt.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            ApiResponse<Chat> response = new ApiResponse<>(false, "Failed to retrieve chat", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Send a message in a chat
     */
    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<Message>> sendMessage(
            @PathVariable Long chatId,
            @RequestBody MessageSendDto messageDto) {
        try {
            Message message = chatService.sendMessage(
                chatId,
                messageDto.getSenderId(),
                messageDto.getContent(),
                messageDto.getMessageType() != null ? messageDto.getMessageType() : Message.MessageType.TEXT
            );
            
            ApiResponse<Message> response = new ApiResponse<>(true, "Message sent successfully", message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<Message> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<Message> response = new ApiResponse<>(false, "Failed to send message", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get messages for a chat
     */
    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<List<Message>>> getChatMessages(
            @PathVariable Long chatId,
            @RequestParam Long userId) {
        try {
            List<Message> messages = chatService.getChatMessages(chatId, userId);
            
            ApiResponse<List<Message>> response = new ApiResponse<>(true, "Messages retrieved successfully", messages);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<List<Message>> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<List<Message>> response = new ApiResponse<>(false, "Failed to retrieve messages", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Mark messages as read
     */
    @PutMapping("/{chatId}/messages/read")
    public ResponseEntity<ApiResponse<String>> markMessagesAsRead(
            @PathVariable Long chatId,
            @RequestParam Long userId) {
        try {
            chatService.markMessagesAsRead(chatId, userId);
            
            ApiResponse<String> response = new ApiResponse<>(true, "Messages marked as read", null);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to mark messages as read", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get user's chats
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Chat>>> getUserChats(@PathVariable Long userId) {
        try {
            List<Chat> chats = chatService.getUserChats(userId);
            
            ApiResponse<List<Chat>> response = new ApiResponse<>(true, "User chats retrieved", chats);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<Chat>> response = new ApiResponse<>(false, "Failed to retrieve user chats", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get unread message count for user
     */
    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(@PathVariable Long userId) {
        try {
            Long count = chatService.getUnreadMessageCount(userId);
            
            ApiResponse<Long> response = new ApiResponse<>(true, "Unread count retrieved", count);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<Long> response = new ApiResponse<>(false, "Failed to get unread count", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Send system message
     */
    @PostMapping("/{chatId}/system-message")
    public ResponseEntity<ApiResponse<Message>> sendSystemMessage(
            @PathVariable Long chatId,
            @RequestParam String content) {
        try {
            Message message = chatService.sendSystemMessage(chatId, content);
            
            ApiResponse<Message> response = new ApiResponse<>(true, "System message sent", message);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<Message> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<Message> response = new ApiResponse<>(false, "Failed to send system message", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
