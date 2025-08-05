package com.zephyros.urbanup.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zephyros.urbanup.model.Chat;
import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;

@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    
    // Find chat by task
    Optional<Chat> findByTask(Task task);
    
    Optional<Chat> findByTaskId(Long taskId);
    
    // Find chats by user (either as poster or fulfiller)
    @Query("SELECT c FROM Chat c WHERE c.poster = :user OR c.fulfiller = :user ORDER BY c.updatedAt DESC")
    List<Chat> findChatsByUser(@Param("user") User user);
    
    // Find chats where user is poster
    List<Chat> findByPosterOrderByUpdatedAtDesc(User poster);
    
    // Find chats where user is fulfiller
    List<Chat> findByFulfillerOrderByUpdatedAtDesc(User fulfiller);
    
    // Find active chats
    List<Chat> findByIsActiveTrueOrderByUpdatedAtDesc();
    
    @Query("SELECT c FROM Chat c WHERE c.isActive = true AND (c.poster = :user OR c.fulfiller = :user) ORDER BY c.updatedAt DESC")
    List<Chat> findActiveChatsByUser(@Param("user") User user);
    
    // Find chats between two specific users
    @Query("SELECT c FROM Chat c WHERE " +
           "(c.poster = :user1 AND c.fulfiller = :user2) OR " +
           "(c.poster = :user2 AND c.fulfiller = :user1)")
    List<Chat> findChatsBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    // Recent chats
    @Query("SELECT c FROM Chat c WHERE c.updatedAt >= :since ORDER BY c.updatedAt DESC")
    List<Chat> findRecentChats(@Param("since") LocalDateTime since);
    
    @Query("SELECT c FROM Chat c WHERE (c.poster = :user OR c.fulfiller = :user) AND c.updatedAt >= :since ORDER BY c.updatedAt DESC")
    List<Chat> findRecentChatsByUser(@Param("user") User user, @Param("since") LocalDateTime since);
    
    // Analytics
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.createdAt >= :startDate AND c.createdAt <= :endDate")
    Long countChatsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(c) FROM Chat c WHERE c.isActive = true")
    Long countActiveChats();
    
    // Chat exists check
    boolean existsByTask(Task task);
    
    boolean existsByTaskId(Long taskId);
}
