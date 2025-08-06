package com.zephyros.urbanup.model;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "notifications")
public class Notification {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(nullable = false)
    @NotBlank(message = "Notification title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Notification message is required")
    @Size(max = 500, message = "Message cannot exceed 500 characters")
    private String message;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationPriority priority = NotificationPriority.NORMAL;
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "is_pushed")
    private Boolean isPushed = false;
    
    // Reference to related entities
    @Column(name = "task_id")
    private Long taskId;
    
    @Column(name = "chat_id")
    private Long chatId;
    
    @Column(name = "payment_id")
    private Long paymentId;
    
    @Column(name = "review_id")
    private Long reviewId;
    
    // Additional data as JSON
    @ElementCollection
    @CollectionTable(name = "notification_data", joinColumns = @JoinColumn(name = "notification_id"))
    @MapKeyColumn(name = "data_key")
    @Column(name = "data_value")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Map<String, String> additionalData = new HashMap<>();
    
    @Column(name = "deep_link_url")
    private String deepLinkUrl;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    @Column(name = "pushed_at")
    private LocalDateTime pushedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    public enum NotificationType {
        TASK_CREATED("Task Created"),
        TASK_ACCEPTED("Task Accepted"),
        TASK_STARTED("Task Started"),
        TASK_COMPLETED("Task Completed"),
        TASK_CONFIRMED("Task Confirmed"),
        TASK_CANCELLED("Task Cancelled"),
        NEW_MESSAGE("New Message"),
        PAYMENT_RECEIVED("Payment Received"),
        PAYMENT_RELEASED("Payment Released"),
        PAYMENT_FAILED("Payment Failed"),
        REVIEW_RECEIVED("Review Received"),
        PROFILE_UPDATE("Profile Update"),
        SYSTEM_ANNOUNCEMENT("System Announcement"),
        DEADLINE_REMINDER("Deadline Reminder"),
        LOCATION_UPDATE("Location Update");
        
        private final String displayName;
        
        NotificationType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum NotificationPriority {
        LOW("Low"),
        NORMAL("Normal"),
        HIGH("High"),
        URGENT("Urgent");
        
        private final String displayName;
        
        NotificationPriority(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public Notification() {}
    
    public Notification(User user, String title, String message, NotificationType type) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
    }
    
    public Notification(User user, String title, String message, NotificationType type, 
                       NotificationPriority priority) {
        this.user = user;
        this.title = title;
        this.message = message;
        this.type = type;
        this.priority = priority;
    }
    
    // Static factory methods
    public static Notification createTaskNotification(User user, String title, String message, 
                                                     NotificationType type, Long taskId) {
        Notification notification = new Notification(user, title, message, type);
        notification.setTaskId(taskId);
        notification.setDeepLinkUrl("/tasks/" + taskId);
        return notification;
    }
    
    public static Notification createMessageNotification(User user, String title, String message, 
                                                        Long chatId, Long taskId) {
        Notification notification = new Notification(user, title, message, NotificationType.NEW_MESSAGE);
        notification.setChatId(chatId);
        notification.setTaskId(taskId);
        notification.setDeepLinkUrl("/chats/" + chatId);
        return notification;
    }
    
    public static Notification createPaymentNotification(User user, String title, String message, 
                                                        NotificationType type, Long paymentId) {
        Notification notification = new Notification(user, title, message, type);
        notification.setPaymentId(paymentId);
        notification.setDeepLinkUrl("/payments/" + paymentId);
        return notification;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public NotificationType getType() { return type; }
    public void setType(NotificationType type) { this.type = type; }
    
    public NotificationPriority getPriority() { return priority; }
    public void setPriority(NotificationPriority priority) { this.priority = priority; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public Boolean getIsPushed() { return isPushed; }
    public void setIsPushed(Boolean isPushed) { this.isPushed = isPushed; }
    
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    
    public Long getChatId() { return chatId; }
    public void setChatId(Long chatId) { this.chatId = chatId; }
    
    public Long getPaymentId() { return paymentId; }
    public void setPaymentId(Long paymentId) { this.paymentId = paymentId; }
    
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }
    
    public Map<String, String> getAdditionalData() { return additionalData; }
    public void setAdditionalData(Map<String, String> additionalData) { this.additionalData = additionalData; }
    
    public String getDeepLinkUrl() { return deepLinkUrl; }
    public void setDeepLinkUrl(String deepLinkUrl) { this.deepLinkUrl = deepLinkUrl; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public LocalDateTime getPushedAt() { return pushedAt; }
    public void setPushedAt(LocalDateTime pushedAt) { this.pushedAt = pushedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    // Helper methods
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    public void markAsPushed() {
        this.isPushed = true;
        this.pushedAt = LocalDateTime.now();
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public void addData(String key, String value) {
        if (this.additionalData == null) {
            this.additionalData = new HashMap<>();
        }
        this.additionalData.put(key, value);
    }
    
    public String getData(String key) {
        return additionalData != null ? additionalData.get(key) : null;
    }
    
    public void setExpiresInHours(int hours) {
        this.expiresAt = LocalDateTime.now().plusHours(hours);
    }
    
    public void setExpiresInDays(int days) {
        this.expiresAt = LocalDateTime.now().plusDays(days);
    }
}
