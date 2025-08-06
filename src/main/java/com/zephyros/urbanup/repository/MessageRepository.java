package com.zephyros.urbanup.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zephyros.urbanup.model.Chat;
import com.zephyros.urbanup.model.Message;
import com.zephyros.urbanup.model.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    
    // Find messages by chat
    List<Message> findByChatOrderByCreatedAtAsc(Chat chat);
    
    List<Message> findByChatOrderByCreatedAtDesc(Chat chat);
    
    Page<Message> findByChatOrderByCreatedAtDesc(Chat chat, Pageable pageable);
    
    List<Message> findByChatIdOrderByCreatedAtAsc(Long chatId);
    
    // Find messages by chat with eager loading
    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.sender LEFT JOIN FETCH m.chat c LEFT JOIN FETCH c.task t LEFT JOIN FETCH c.poster LEFT JOIN FETCH c.fulfiller WHERE m.chat = :chat ORDER BY m.createdAt ASC")
    List<Message> findByChatWithEagerLoading(@Param("chat") Chat chat);
    
    @Query("SELECT m FROM Message m LEFT JOIN FETCH m.sender LEFT JOIN FETCH m.chat c LEFT JOIN FETCH c.task WHERE m.chat.id = :chatId ORDER BY m.createdAt ASC")
    List<Message> findByChatIdWithEagerLoading(@Param("chatId") Long chatId);
    
    // Find messages by sender
    List<Message> findBySenderOrderByCreatedAtDesc(User sender);
    
    // Find unread messages
    List<Message> findByIsReadFalse();
    
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND m.isRead = false AND m.sender != :user ORDER BY m.createdAt ASC")
    List<Message> findUnreadMessagesInChat(@Param("chat") Chat chat, @Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE m.chat.id IN " +
           "(SELECT c.id FROM Chat c WHERE c.poster = :user OR c.fulfiller = :user) " +
           "AND m.isRead = false AND m.sender != :user ORDER BY m.createdAt DESC")
    List<Message> findUnreadMessagesForUser(@Param("user") User user);
    
    @Query("SELECT m FROM Message m WHERE m.chat.id = :chatId AND m.isRead = false AND m.sender.id != :recipientId")
    List<Message> findUnreadMessagesByChatIdAndRecipientId(@Param("chatId") Long chatId, @Param("recipientId") Long recipientId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id IN " +
           "(SELECT c.id FROM Chat c WHERE c.poster = :user OR c.fulfiller = :user) " +
           "AND m.isRead = false AND m.sender != :user")
    Long countUnreadMessagesForUser(@Param("user") User user);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat.id IN (SELECT c.id FROM Chat c WHERE c.poster.id = :userId OR c.fulfiller.id = :userId) AND m.isRead = false AND m.sender.id != :userId")
    Long countUnreadMessagesByUserId(@Param("userId") Long userId);

    // Find messages by type
    List<Message> findByMessageType(Message.MessageType messageType);
    
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND m.messageType = :messageType ORDER BY m.createdAt ASC")
    List<Message> findMessagesByTypeInChat(@Param("chat") Chat chat, @Param("messageType") Message.MessageType messageType);
    
    // System messages
    List<Message> findByIsSystemMessageTrueOrderByCreatedAtDesc();
    
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND m.isSystemMessage = true ORDER BY m.createdAt ASC")
    List<Message> findSystemMessagesInChat(@Param("chat") Chat chat);
    
    // Recent messages
    @Query("SELECT m FROM Message m WHERE m.createdAt >= :since ORDER BY m.createdAt DESC")
    List<Message> findRecentMessages(@Param("since") LocalDateTime since);
    
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND m.createdAt >= :since ORDER BY m.createdAt ASC")
    List<Message> findRecentMessagesInChat(@Param("chat") Chat chat, @Param("since") LocalDateTime since);
    
    // Messages with attachments
    @Query("SELECT m FROM Message m WHERE SIZE(m.attachmentUrls) > 0 ORDER BY m.createdAt DESC")
    List<Message> findMessagesWithAttachments();
    
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND SIZE(m.attachmentUrls) > 0 ORDER BY m.createdAt ASC")
    List<Message> findMessagesWithAttachmentsInChat(@Param("chat") Chat chat);
    
    // Search messages
    @Query("SELECT m FROM Message m WHERE LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY m.createdAt DESC")
    List<Message> searchMessagesByContent(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT m FROM Message m WHERE m.chat = :chat AND LOWER(m.content) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY m.createdAt ASC")
    List<Message> searchMessagesInChat(@Param("chat") Chat chat, @Param("searchTerm") String searchTerm);
    
    // Get last message in chat
    @Query("SELECT m FROM Message m WHERE m.chat = :chat ORDER BY m.createdAt DESC LIMIT 1")
    Message findLastMessageInChat(@Param("chat") Chat chat);
    
    // Analytics
    @Query("SELECT COUNT(m) FROM Message m WHERE m.createdAt >= :startDate AND m.createdAt <= :endDate")
    Long countMessagesCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.chat = :chat")
    Long countMessagesInChat(@Param("chat") Chat chat);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.sender = :user")
    Long countMessagesBySender(@Param("user") User user);
    
    // Mark messages as read
    @Query("UPDATE Message m SET m.isRead = true, m.readAt = :readAt WHERE m.chat = :chat AND m.sender != :user AND m.isRead = false")
    void markMessagesAsReadInChat(@Param("chat") Chat chat, @Param("user") User user, @Param("readAt") LocalDateTime readAt);
}
