package com.zephyros.urbanup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.zephyros.urbanup.model.Chat;
import com.zephyros.urbanup.model.Message;
import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.repository.ChatRepository;
import com.zephyros.urbanup.repository.MessageRepository;
import com.zephyros.urbanup.repository.TaskRepository;
import com.zephyros.urbanup.repository.UserRepository;

@Service
@Transactional
public class RealtimeChatService {
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * Create or get chat between users for a task
     */
    public Chat createOrGetChat(Long taskId, Long fulfillerId) {
        // Get task and validate
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found");
        }
        
        Optional<User> fulfillerOpt = userRepository.findById(fulfillerId);
        if (fulfillerOpt.isEmpty()) {
            throw new IllegalArgumentException("Fulfiller not found");
        }
        
        Task task = taskOpt.get();
        User fulfiller = fulfillerOpt.get();
        
        // Check if chat already exists
        Optional<Chat> existingChat = chatRepository.findByTaskId(taskId);
        if (existingChat.isPresent()) {
            return existingChat.get();
        }
        
        // Create new chat
        Chat chat = new Chat();
        chat.setTask(task);
        chat.setPoster(task.getPoster());
        chat.setFulfiller(fulfiller);
        chat.setIsActive(true);
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());
        
        Chat savedChat = chatRepository.save(chat);
        
        // Send system message about chat creation
        sendSystemMessage(savedChat.getId(), "Chat initiated between " + 
            task.getPoster().getFirstName() + " and " + fulfiller.getFirstName());
        
        return savedChat;
    }
    
    /**
     * Send a real-time message
     */
    public Message sendRealtimeMessage(Long chatId, Long senderId, String content, 
                                     Message.MessageType messageType) {
        // Validate chat and sender
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        Optional<User> senderOpt = userRepository.findById(senderId);
        
        if (chatOpt.isEmpty()) {
            throw new IllegalArgumentException("Chat not found");
        }
        
        if (senderOpt.isEmpty()) {
            throw new IllegalArgumentException("Sender not found");
        }
        
        Chat chat = chatOpt.get();
        User sender = senderOpt.get();
        
        // Validate sender has access to this chat
        if (!sender.getId().equals(chat.getPoster().getId()) && 
            !sender.getId().equals(chat.getFulfiller().getId())) {
            throw new IllegalArgumentException("User not authorized to send messages in this chat");
        }
        
        // Create and save message
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setIsRead(false);
        message.setCreatedAt(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        
        // Update chat timestamp
        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);
        
        // Broadcast message via WebSocket
        broadcastMessage(chatId, savedMessage);
        
        // Send notification to the other participant
        User recipient = sender.getId().equals(chat.getPoster().getId()) ? 
            chat.getFulfiller() : chat.getPoster();
        
        notificationService.sendNewMessageNotification(recipient, chat, 
            sender.getFirstName() + " " + sender.getLastName());
        
        return savedMessage;
    }
    
    /**
     * Send system message
     */
    public Message sendSystemMessage(Long chatId, String content) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new IllegalArgumentException("Chat not found");
        }
        
        Chat chat = chatOpt.get();
        
        Message message = new Message();
        message.setChat(chat);
        message.setSender(null); // System message has no sender
        message.setContent(content);
        message.setMessageType(Message.MessageType.SYSTEM);
        message.setIsRead(true); // System messages are automatically read
        message.setCreatedAt(LocalDateTime.now());
        
        Message savedMessage = messageRepository.save(message);
        
        // Broadcast system message
        broadcastMessage(chatId, savedMessage);
        
        return savedMessage;
    }
    
    /**
     * Upload and send media message
     */
    public Message sendMediaMessage(Long chatId, Long senderId, MultipartFile file, 
                                  String caption) {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        if (file.getSize() > 10 * 1024 * 1024) { // 10MB limit
            throw new IllegalArgumentException("File size exceeds 10MB limit");
        }
        
        // TODO: Implement actual file storage (AWS S3, local storage, etc.)
        // For now, create a placeholder URL
        String fileUrl = "/uploads/" + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        
        // Determine message type based on file content
        String contentType = file.getContentType();
        Message.MessageType messageType = Message.MessageType.FILE;
        
        if (contentType != null) {
            if (contentType.startsWith("image/")) {
                messageType = Message.MessageType.IMAGE;
            }
        }
        
        // Create message content with file info
        String messageContent = caption != null ? caption : file.getOriginalFilename();
        
        Message message = sendRealtimeMessage(chatId, senderId, messageContent, messageType);
        
        // Add attachment URL to the message
        message.getAttachmentUrls().add(fileUrl);
        messageRepository.save(message);
        
        return message;
    }
    
    /**
     * Get chat messages with pagination
     */
    @Transactional(readOnly = true)
    public List<Message> getChatMessages(Long chatId, Long userId, int page, int size) {
        // Validate user has access to chat
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new IllegalArgumentException("Chat not found");
        }
        
        Chat chat = chatOpt.get();
        if (!userId.equals(chat.getPoster().getId()) && 
            !userId.equals(chat.getFulfiller().getId())) {
            throw new IllegalArgumentException("User not authorized to access this chat");
        }
        
        // Get messages with pagination
        return messageRepository.findByChatIdOrderByCreatedAtAsc(chatId);
    }
    
    /**
     * Mark messages as read
     */
    public void markMessagesAsRead(Long chatId, Long userId) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        if (chatOpt.isEmpty()) {
            throw new IllegalArgumentException("Chat not found");
        }
        
        Chat chat = chatOpt.get();
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        User user = userOpt.get();
        if (!userId.equals(chat.getPoster().getId()) && 
            !userId.equals(chat.getFulfiller().getId())) {
            throw new IllegalArgumentException("User not authorized to access this chat");
        }
        
        // Mark all unread messages from the other user as read
        List<Message> unreadMessages = messageRepository.findUnreadMessagesInChat(chat, user);
        
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
        }
        
        messageRepository.saveAll(unreadMessages);
        
        // Broadcast read status update
        broadcastReadStatus(chatId, userId, unreadMessages.size());
    }
    
    /**
     * Get user's chats
     */
    @Transactional(readOnly = true)
    public List<Chat> getUserChats(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        return chatRepository.findChatsByUser(userOpt.get());
    }
    
    /**
     * Get unread message count for user
     */
    @Transactional(readOnly = true)
    public long getUnreadMessageCount(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        
        return messageRepository.countUnreadMessagesForUser(userOpt.get());
    }
    
    /**
     * Broadcast message to WebSocket subscribers
     */
    private void broadcastMessage(Long chatId, Message message) {
        try {
            // Create a simplified message object for broadcasting
            MessageBroadcast broadcast = new MessageBroadcast();
            broadcast.setId(message.getId());
            broadcast.setChatId(chatId);
            broadcast.setSenderId(message.getSender() != null ? message.getSender().getId() : null);
            broadcast.setSenderName(message.getSender() != null ? 
                message.getSender().getFirstName() + " " + message.getSender().getLastName() : "System");
            broadcast.setContent(message.getContent());
            broadcast.setMessageType(message.getMessageType().toString());
            broadcast.setAttachmentUrl(message.getAttachmentUrls().isEmpty() ? null : message.getAttachmentUrls().get(0));
            broadcast.setCreatedAt(message.getCreatedAt());
            broadcast.setIsRead(message.getIsRead());
            
            // Broadcast to chat subscribers
            messagingTemplate.convertAndSend("/topic/chat/" + chatId, broadcast);
        } catch (Exception e) {
            // Log error but don't fail the message send
            System.err.println("Failed to broadcast message: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast read status update
     */
    private void broadcastReadStatus(Long chatId, Long userId, int messageCount) {
        try {
            ReadStatusUpdate update = new ReadStatusUpdate();
            update.setChatId(chatId);
            update.setUserId(userId);
            update.setMessageCount(messageCount);
            update.setTimestamp(LocalDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/read", update);
        } catch (Exception e) {
            System.err.println("Failed to broadcast read status: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast typing indicator
     */
    public void broadcastTypingIndicator(Long chatId, Long userId, boolean isTyping) {
        try {
            TypingIndicator indicator = new TypingIndicator();
            indicator.setChatId(chatId);
            indicator.setUserId(userId);
            indicator.setIsTyping(isTyping);
            indicator.setTimestamp(LocalDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/typing", indicator);
        } catch (Exception e) {
            System.err.println("Failed to broadcast typing indicator: " + e.getMessage());
        }
    }
    
    /**
     * Broadcast user presence
     */
    public void broadcastUserPresence(Long chatId, Long userId, String status) {
        try {
            UserPresence presence = new UserPresence();
            presence.setChatId(chatId);
            presence.setUserId(userId);
            presence.setStatus(status);
            presence.setTimestamp(LocalDateTime.now());
            
            messagingTemplate.convertAndSend("/topic/chat/" + chatId + "/presence", presence);
        } catch (Exception e) {
            System.err.println("Failed to broadcast user presence: " + e.getMessage());
        }
    }
    
    // Inner classes for WebSocket messages
    public static class MessageBroadcast {
        private Long id;
        private Long chatId;
        private Long senderId;
        private String senderName;
        private String content;
        private String messageType;
        private String attachmentUrl;
        private LocalDateTime createdAt;
        private Boolean isRead;
        
        // Getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        
        public Long getChatId() { return chatId; }
        public void setChatId(Long chatId) { this.chatId = chatId; }
        
        public Long getSenderId() { return senderId; }
        public void setSenderId(Long senderId) { this.senderId = senderId; }
        
        public String getSenderName() { return senderName; }
        public void setSenderName(String senderName) { this.senderName = senderName; }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        
        public String getMessageType() { return messageType; }
        public void setMessageType(String messageType) { this.messageType = messageType; }
        
        public String getAttachmentUrl() { return attachmentUrl; }
        public void setAttachmentUrl(String attachmentUrl) { this.attachmentUrl = attachmentUrl; }
        
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        
        public Boolean getIsRead() { return isRead; }
        public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    }
    
    public static class ReadStatusUpdate {
        private Long chatId;
        private Long userId;
        private int messageCount;
        private LocalDateTime timestamp;
        
        // Getters and setters
        public Long getChatId() { return chatId; }
        public void setChatId(Long chatId) { this.chatId = chatId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public int getMessageCount() { return messageCount; }
        public void setMessageCount(int messageCount) { this.messageCount = messageCount; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    public static class TypingIndicator {
        private Long chatId;
        private Long userId;
        private boolean isTyping;
        private LocalDateTime timestamp;
        
        // Getters and setters
        public Long getChatId() { return chatId; }
        public void setChatId(Long chatId) { this.chatId = chatId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public boolean isTyping() { return isTyping; }
        public void setIsTyping(boolean isTyping) { this.isTyping = isTyping; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
    
    public static class UserPresence {
        private Long chatId;
        private Long userId;
        private String status;
        private LocalDateTime timestamp;
        
        // Getters and setters
        public Long getChatId() { return chatId; }
        public void setChatId(Long chatId) { this.chatId = chatId; }
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }
}
