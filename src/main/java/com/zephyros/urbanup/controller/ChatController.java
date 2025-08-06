package com.zephyros.urbanup.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
import org.springframework.web.multipart.MultipartFile;

import com.zephyros.urbanup.dto.ApiResponse;
import com.zephyros.urbanup.dto.ChatCreateDto;
import com.zephyros.urbanup.dto.ChatResponseDto;
import com.zephyros.urbanup.dto.MessageResponseDto;
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

    // Helper method to convert a Message entity to a MessageResponseDto
    private MessageResponseDto convertToMessageDto(Message message) {
        return new MessageResponseDto(
                message.getId(),
                message.getContent(),
                message.getMessageType().toString(),
                message.getCreatedAt(),
                message.getSender() != null ? message.getSender().getFirstName() + " " + message.getSender().getLastName() : "System",
                message.getSender() != null ? message.getSender().getId() : null,
                message.getIsRead()
        );
    }

    // Helper method to convert a Chat entity to a ChatResponseDto
    private ChatResponseDto convertToChatDto(Chat chat, Long currentUserId) {
        User otherParticipant = chat.getPoster().getId().equals(currentUserId) ? chat.getFulfiller() : chat.getPoster();
        String otherParticipantName = otherParticipant != null ? otherParticipant.getFirstName() + " " + otherParticipant.getLastName() : "N/A";
        Long otherParticipantId = otherParticipant != null ? otherParticipant.getId() : null;

        // Eagerly fetch messages to avoid LazyInitializationException
        List<Message> messages = chatService.getChatMessages(chat.getId(), currentUserId);
        List<MessageResponseDto> messageDTOs = messages.stream()
                .map(this::convertToMessageDto)
                .collect(Collectors.toList());

        return new ChatResponseDto(
                chat.getId(),
                chat.getTask().getTitle(),
                otherParticipantId,
                otherParticipantName,
                messageDTOs,
                chat.getUpdatedAt()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponseDto>> createOrGetChat(@RequestBody ChatCreateDto chatDto) {
        try {
            Optional<Task> taskOpt = taskService.getTaskById(chatDto.getTaskId());
            Optional<User> userOpt = userService.getUserById(chatDto.getFulfillerId());

            if (taskOpt.isEmpty() || userOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Task or User not found", null));
            }

            Chat chat = chatService.getOrCreateTaskChat(taskOpt.get(), userOpt.get());
            ChatResponseDto chatResponseDto = convertToChatDto(chat, taskOpt.get().getPoster().getId()); // Assuming poster initiates

            return ResponseEntity.ok(new ApiResponse<>(true, "Chat created/retrieved successfully", chatResponseDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to create/get chat: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ApiResponse<ChatResponseDto>> getChat(@PathVariable Long chatId, @RequestParam Long userId) {
        try {
            Optional<Chat> chatOpt = chatService.getChatById(chatId, userId);
            if (chatOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, "Chat not found or user lacks access", null));
            }
            ChatResponseDto chatDto = convertToChatDto(chatOpt.get(), userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Chat found", chatDto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to retrieve chat: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<MessageResponseDto>> sendMessage(@PathVariable Long chatId, @RequestBody MessageSendDto messageDto) {
        try {
            Message message = chatService.sendMessage(
                    chatId,
                    messageDto.getSenderId(),
                    messageDto.getContent(),
                    messageDto.getMessageType() != null ? messageDto.getMessageType() : Message.MessageType.TEXT
            );
            MessageResponseDto messageResponseDto = convertToMessageDto(message);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Message sent successfully", messageResponseDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to send message: " + e.getMessage(), null));
        }
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponseDto>>> getChatMessages(@PathVariable Long chatId, @RequestParam Long userId) {
        try {
            List<Message> messages = chatService.getChatMessages(chatId, userId);
            List<MessageResponseDto> messageDTOs = messages.stream()
                    .map(this::convertToMessageDto)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>(true, "Messages retrieved successfully", messageDTOs));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to retrieve messages: " + e.getMessage(), null));
        }
    }

    @PutMapping("/{chatId}/messages/read")
    public ResponseEntity<ApiResponse<String>> markMessagesAsRead(@PathVariable Long chatId, @RequestParam Long userId) {
        try {
            chatService.markMessagesAsRead(chatId, userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Messages marked as read", null));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to mark messages as read: " + e.getMessage(), null));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<ChatResponseDto>>> getUserChats(@PathVariable Long userId) {
        try {
            List<Chat> chats = chatService.getUserChats(userId);
            List<ChatResponseDto> chatDTOs = chats.stream()
                    .map(chat -> convertToChatDto(chat, userId))
                    .collect(Collectors.toList());
            return ResponseEntity.ok(new ApiResponse<>(true, "User chats retrieved", chatDTOs));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to retrieve user chats: " + e.getMessage(), null));
        }
    }

    @GetMapping("/user/{userId}/unread-count")
    public ResponseEntity<ApiResponse<Long>> getUnreadMessageCount(@PathVariable Long userId) {
        try {
            Long count = chatService.getUnreadMessageCount(userId);
            return ResponseEntity.ok(new ApiResponse<>(true, "Unread count retrieved", count));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to get unread count: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{chatId}/media")
    public ResponseEntity<ApiResponse<MessageResponseDto>> uploadChatMedia(
            @PathVariable Long chatId,
            @RequestParam("file") MultipartFile file,
            @RequestParam Long senderId,
            @RequestParam(required = false) String caption) {
        try {
            if (file.isEmpty() || file.getSize() > 10 * 1024 * 1024) { // 10MB limit
                return ResponseEntity.badRequest().body(new ApiResponse<>(false, "File is empty or too large (max 10MB)", null));
            }

            String contentType = file.getContentType();
            Message.MessageType messageType = Message.MessageType.FILE;
            if (contentType != null && contentType.startsWith("image/")) {
                messageType = Message.MessageType.IMAGE;
            }

            // Placeholder for file storage logic
            String fileName = file.getOriginalFilename();
            String fileUrl = "/uploads/chat/" + chatId + "/" + System.currentTimeMillis() + "_" + fileName;
            String messageContent = (caption != null && !caption.trim().isEmpty() ? caption : fileName) + "::" + fileUrl;

            Message message = chatService.sendMessage(chatId, senderId, messageContent, messageType);
            MessageResponseDto messageDto = convertToMessageDto(message);

            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "Media uploaded successfully", messageDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to upload media: " + e.getMessage(), null));
        }
    }

    @PostMapping("/{chatId}/system-message")
    public ResponseEntity<ApiResponse<MessageResponseDto>> sendSystemMessage(@PathVariable Long chatId, @RequestParam String content) {
        try {
            Message message = chatService.sendSystemMessage(chatId, content);
            MessageResponseDto messageDto = convertToMessageDto(message);
            return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, "System message sent", messageDto));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, e.getMessage(), null));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse<>(false, "Failed to send system message: " + e.getMessage(), null));
        }
    }
}
