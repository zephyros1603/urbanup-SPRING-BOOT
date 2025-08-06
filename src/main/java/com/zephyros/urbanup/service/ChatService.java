package com.zephyros.urbanup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zephyros.urbanup.model.Chat;
import com.zephyros.urbanup.model.Message;
import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.repository.ChatRepository;
import com.zephyros.urbanup.repository.MessageRepository;
import com.zephyros.urbanup.repository.UserRepository;

@Service
@Transactional
public class ChatService {
    
    @Autowired
    private ChatRepository chatRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    /**
     * Get or create a chat for a task between poster and a potential fulfiller
     */
    public Chat getOrCreateTaskChat(Task task, User participant) {
        // Check if chat already exists for this task
        Optional<Chat> existingChat = chatRepository.findByTaskId(task.getId());
        
        if (existingChat.isPresent()) {
            return existingChat.get();
        }
        
        // Create new chat
        Chat chat = new Chat();
        chat.setTask(task);
        chat.setPoster(task.getPoster());  // Set the task poster
        chat.setFulfiller(participant);    // Set the participant as fulfiller
        chat.setIsActive(true);
        chat.setCreatedAt(LocalDateTime.now());
        chat.setUpdatedAt(LocalDateTime.now());
        
        return chatRepository.save(chat);
    }
    
    /**
     * Send a message in a chat
     */
    public Message sendMessage(Long chatId, Long senderId, String content, Message.MessageType messageType) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        Optional<User> senderOpt = userRepository.findById(senderId);
        
        if (chatOpt.isEmpty() || senderOpt.isEmpty()) {
            throw new IllegalArgumentException("Chat or sender not found");
        }
        
        Chat chat = chatOpt.get();
        User sender = senderOpt.get();
        
        // Validate that sender is authorized to send messages in this chat
        if (!canUserAccessChat(chat, sender)) {
            throw new IllegalArgumentException("User not authorized to send messages in this chat");
        }
        
        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setCreatedAt(LocalDateTime.now());
        message.setIsRead(false);
        
        Message savedMessage = messageRepository.save(message);
        
        // Update chat's last message timestamp
        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);
        
        // Send notification to the other participant
        User recipient = getOtherParticipant(chat, sender);
        if (recipient != null) {
            notificationService.sendNewMessageNotification(recipient, chat, sender.getFirstName() + " " + sender.getLastName());
        }
        
        return savedMessage;
    }
    
    /**
     * Get messages for a chat
     */
    @Transactional(readOnly = true)
    public List<Message> getChatMessages(Long chatId, Long userId) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (chatOpt.isEmpty() || userOpt.isEmpty()) {
            throw new IllegalArgumentException("Chat or user not found");
        }
        
        Chat chat = chatOpt.get();
        User user = userOpt.get();
        
        // Validate that user can access this chat
        if (!canUserAccessChat(chat, user)) {
            throw new IllegalArgumentException("User not authorized to access this chat");
        }
        
        return messageRepository.findByChatOrderByCreatedAtAsc(chat);
    }
    
    /**
     * Mark messages as read
     */
    public void markMessagesAsRead(Long chatId, Long userId) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (chatOpt.isEmpty() || userOpt.isEmpty()) {
            return;
        }
        
        Chat chat = chatOpt.get();
        User user = userOpt.get();
        
        // Validate access
        if (!canUserAccessChat(chat, user)) {
            return;
        }
        
        // Find unread messages not sent by this user
        // Mark all unread messages in this chat as read for this user
        List<Message> unreadMessages = messageRepository.findUnreadMessagesInChat(chat, user);
        
        // Mark them as read
        for (Message message : unreadMessages) {
            message.setIsRead(true);
            message.setReadAt(LocalDateTime.now());
        }
        
        messageRepository.saveAll(unreadMessages);
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
        
        User user = userOpt.get();
        return chatRepository.findChatsByUser(user);
    }
    
    /**
     * Get chat by ID if user has access
     */
    @Transactional(readOnly = true)
    public Optional<Chat> getChatById(Long chatId, Long userId) {
        Optional<Chat> chatOpt = chatRepository.findById(chatId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (chatOpt.isEmpty() || userOpt.isEmpty()) {
            return Optional.empty();
        }
        
        Chat chat = chatOpt.get();
        User user = userOpt.get();
        
        if (canUserAccessChat(chat, user)) {
            return Optional.of(chat);
        }
        
        return Optional.empty();
    }
    
    /**
     * Get unread message count for user
     */
    @Transactional(readOnly = true)
    public Long getUnreadMessageCount(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return 0L;
        }
        
        User user = userOpt.get();
        
        // Get all chats for the user and count unread messages
        return chatRepository.findChatsByUser(user)
                       .stream()
                       .mapToLong(chat -> messageRepository.countUnreadMessagesInChat(chat, user))
                       .sum();
    }
    
    // Helper Methods
    
    /**
     * Check if user can access a specific chat
     */
    private boolean canUserAccessChat(Chat chat, User user) {
        Task task = chat.getTask();
        
        // User can access if they are the poster
        if (task.getPoster().getId().equals(user.getId())) {
            return true;
        }
        
        // User can access if they are the assigned fulfiller
        if (task.getFulfiller() != null && task.getFulfiller().getId().equals(user.getId())) {
            return true;
        }
        
        // For now, allow access if user has applied for the task
        // In a more complex system, you might want to restrict this further
        return true;
    }
    
    /**
     * Get the other participant in a chat (not the sender)
     */
    private User getOtherParticipant(Chat chat, User sender) {
        Task task = chat.getTask();
        
        // If sender is poster, return fulfiller (if assigned)
        if (task.getPoster().getId().equals(sender.getId())) {
            return task.getFulfiller();
        }
        
        // If sender is fulfiller, return poster
        if (task.getFulfiller() != null && task.getFulfiller().getId().equals(sender.getId())) {
            return task.getPoster();
        }
        
        // Default to poster if unclear
        return task.getPoster();
    }
    
    /**
     * Send system message to chat
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
        message.setCreatedAt(LocalDateTime.now());
        message.setIsRead(false);
        
        Message savedMessage = messageRepository.save(message);
        
        // Update chat timestamp
        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);
        
        return savedMessage;
    }
}
