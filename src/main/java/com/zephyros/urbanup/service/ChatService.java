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

    public Chat getOrCreateTaskChat(Task task, User participant) {
        return chatRepository.findByTaskId(task.getId()).orElseGet(() -> {
            Chat newChat = new Chat();
            newChat.setTask(task);
            newChat.setPoster(task.getPoster());
            newChat.setFulfiller(participant);
            newChat.setIsActive(true);
            newChat.setCreatedAt(LocalDateTime.now());
            newChat.setUpdatedAt(LocalDateTime.now());
            return chatRepository.save(newChat);
        });
    }

    public Message sendMessage(Long chatId, Long senderId, String content, Message.MessageType messageType) {
        Chat chat = chatRepository.findByIdWithTaskAndUsers(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        boolean isParticipant = chat.getPoster().getId().equals(senderId) ||
                (chat.getFulfiller() != null && chat.getFulfiller().getId().equals(senderId));

        if (!isParticipant) {
            throw new IllegalArgumentException("User not authorized to send messages in this chat");
        }

        Message message = new Message();
        message.setChat(chat);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(messageType);
        message.setCreatedAt(LocalDateTime.now());
        message.setIsRead(false);

        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);

        return messageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<Message> getChatMessages(Long chatId, Long userId) {
        Chat chat = chatRepository.findByIdWithTaskAndUsers(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        boolean isParticipant = chat.getPoster().getId().equals(userId) ||
                (chat.getFulfiller() != null && chat.getFulfiller().getId().equals(userId));

        if (!isParticipant) {
            throw new IllegalArgumentException("User not authorized to view this chat");
        }
        return messageRepository.findByChatIdWithEagerLoading(chatId);
    }

    public void markMessagesAsRead(Long chatId, Long userId) {
        List<Message> messagesToUpdate = messageRepository.findUnreadMessagesByChatIdAndRecipientId(chatId, userId);
        if (!messagesToUpdate.isEmpty()) {
            for (Message message : messagesToUpdate) {
                message.setIsRead(true);
                message.setReadAt(LocalDateTime.now());
            }
            messageRepository.saveAll(messagesToUpdate);
        }
    }

    @Transactional(readOnly = true)
    public List<Chat> getUserChats(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return chatRepository.findChatsByUserWithEagerLoading(user);
    }

    @Transactional(readOnly = true)
    public Optional<Chat> getChatById(Long chatId, Long userId) {
        return chatRepository.findByIdWithTaskAndUsers(chatId)
                .filter(chat -> chat.getPoster().getId().equals(userId) ||
                        (chat.getFulfiller() != null && chat.getFulfiller().getId().equals(userId)));
    }

    @Transactional(readOnly = true)
    public Long getUnreadMessageCount(Long userId) {
        return messageRepository.countUnreadMessagesByUserId(userId);
    }

    public Message sendSystemMessage(Long chatId, String content) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new IllegalArgumentException("Chat not found"));

        Message message = new Message();
        message.setChat(chat);
        message.setSender(null); // System message
        message.setContent(content);
        message.setMessageType(Message.MessageType.SYSTEM);
        message.setCreatedAt(LocalDateTime.now());
        message.setIsRead(true); // System messages are considered read

        chat.setUpdatedAt(LocalDateTime.now());
        chatRepository.save(chat);

        return messageRepository.save(message);
    }
}
