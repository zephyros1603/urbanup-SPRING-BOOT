package com.zephyros.urbanup.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zephyros.urbanup.model.Notification;
import com.zephyros.urbanup.model.User;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    
    // Find notifications by user
    List<Notification> findByUserOrderByCreatedAtDesc(User user);
    
    Page<Notification> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);
    
    // Find unread notifications
    List<Notification> findByIsReadFalse();
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsForUser(@Param("user") User user);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = false ORDER BY n.priority DESC, n.createdAt DESC")
    List<Notification> findUnreadNotificationsForUserByPriority(@Param("user") User user);
    
    // Find read notifications
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isRead = true ORDER BY n.readAt DESC")
    List<Notification> findReadNotificationsForUser(@Param("user") User user);
    
    // Count unread notifications
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user AND n.isRead = false")
    Long countUnreadNotificationsForUser(@Param("user") User user);
    
    // Find notifications by type
    List<Notification> findByType(Notification.NotificationType type);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.type = :type ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByUserAndType(@Param("user") User user, 
                                                     @Param("type") Notification.NotificationType type);
    
    // Find notifications by priority
    List<Notification> findByPriority(Notification.NotificationPriority priority);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.priority = :priority ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByUserAndPriority(@Param("user") User user, 
                                                         @Param("priority") Notification.NotificationPriority priority);
    
    // Find urgent notifications
    @Query("SELECT n FROM Notification n WHERE n.priority = 'URGENT' AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadUrgentNotifications();
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.priority = 'URGENT' AND n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadUrgentNotificationsForUser(@Param("user") User user);
    
    // Find notifications related to specific entities
    @Query("SELECT n FROM Notification n WHERE n.taskId = :taskId ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByTaskId(@Param("taskId") Long taskId);
    
    @Query("SELECT n FROM Notification n WHERE n.chatId = :chatId ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByChatId(@Param("chatId") Long chatId);
    
    @Query("SELECT n FROM Notification n WHERE n.paymentId = :paymentId ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByPaymentId(@Param("paymentId") Long paymentId);
    
    @Query("SELECT n FROM Notification n WHERE n.reviewId = :reviewId ORDER BY n.createdAt DESC")
    List<Notification> findNotificationsByReviewId(@Param("reviewId") Long reviewId);
    
    // Find notifications that haven't been pushed
    @Query("SELECT n FROM Notification n WHERE n.isPushed = false ORDER BY n.priority DESC, n.createdAt ASC")
    List<Notification> findNotificationsToBePushed();
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.isPushed = false ORDER BY n.priority DESC, n.createdAt ASC")
    List<Notification> findNotificationsToBePushedForUser(@Param("user") User user);
    
    // Recent notifications
    @Query("SELECT n FROM Notification n WHERE n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotifications(@Param("since") LocalDateTime since);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.createdAt >= :since ORDER BY n.createdAt DESC")
    List<Notification> findRecentNotificationsForUser(@Param("user") User user, @Param("since") LocalDateTime since);
    
    // Expired notifications
    @Query("SELECT n FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < :now")
    List<Notification> findExpiredNotifications(@Param("now") LocalDateTime now);
    
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND n.expiresAt IS NOT NULL AND n.expiresAt < :now")
    List<Notification> findExpiredNotificationsForUser(@Param("user") User user, @Param("now") LocalDateTime now);
    
    // Search notifications
    @Query("SELECT n FROM Notification n WHERE n.user = :user AND " +
           "(LOWER(n.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(n.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
           "ORDER BY n.createdAt DESC")
    List<Notification> searchNotificationsForUser(@Param("user") User user, @Param("searchTerm") String searchTerm);
    
    // Mark notifications as read
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.user = :user AND n.isRead = false")
    void markAllNotificationsAsReadForUser(@Param("user") User user, @Param("readAt") LocalDateTime readAt);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.id = :notificationId")
    void markNotificationAsRead(@Param("notificationId") Long notificationId, @Param("readAt") LocalDateTime readAt);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true, n.readAt = :readAt WHERE n.user = :user AND n.type = :type AND n.isRead = false")
    void markNotificationsByTypeAsReadForUser(@Param("user") User user, 
                                            @Param("type") Notification.NotificationType type, 
                                            @Param("readAt") LocalDateTime readAt);
    
    // Mark notifications as pushed
    @Modifying
    @Query("UPDATE Notification n SET n.isPushed = true, n.pushedAt = :pushedAt WHERE n.id = :notificationId")
    void markNotificationAsPushed(@Param("notificationId") Long notificationId, @Param("pushedAt") LocalDateTime pushedAt);
    
    @Modifying
    @Query("UPDATE Notification n SET n.isPushed = true, n.pushedAt = :pushedAt WHERE n.id IN :notificationIds")
    void markNotificationsAsPushed(@Param("notificationIds") List<Long> notificationIds, @Param("pushedAt") LocalDateTime pushedAt);
    
    // Delete expired notifications
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.expiresAt IS NOT NULL AND n.expiresAt < :now")
    void deleteExpiredNotifications(@Param("now") LocalDateTime now);
    
    // Analytics
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.user = :user")
    Long countNotificationsForUser(@Param("user") User user);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.type = :type")
    Long countNotificationsByType(@Param("type") Notification.NotificationType type);
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.createdAt >= :startDate AND n.createdAt <= :endDate")
    Long countNotificationsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                         @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT n.type, COUNT(n) FROM Notification n GROUP BY n.type")
    List<Object[]> getNotificationTypeDistribution();
    
    @Query("SELECT n.priority, COUNT(n) FROM Notification n GROUP BY n.priority")
    List<Object[]> getNotificationPriorityDistribution();
    
    // Notification delivery statistics
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isPushed = true")
    Long countPushedNotifications();
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isPushed = false")
    Long countUnpushedNotifications();
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isRead = true")
    Long countReadNotifications();
    
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isRead = false")
    Long countUnreadNotifications();
}
