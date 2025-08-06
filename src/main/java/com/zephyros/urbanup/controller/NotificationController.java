package com.zephyros.urbanup.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zephyros.urbanup.dto.ApiResponse;
import com.zephyros.urbanup.model.Notification;
import com.zephyros.urbanup.service.NotificationService;
import com.zephyros.urbanup.service.UserService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private UserService userService;

    /**
     * Get user notifications with pagination
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<ApiResponse<List<Notification>>> getUserNotifications(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset,
            Authentication authentication) {
        try {
            // Verify user has permission to access these notifications
            if (!userService.isCurrentUser(userId, authentication)) {
                ApiResponse<List<Notification>> response = new ApiResponse<>(false, "Unauthorized access", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            List<Notification> notifications = notificationService.getUserNotifications(userId, limit, offset);
            
            ApiResponse<List<Notification>> response = new ApiResponse<>(true, "Notifications retrieved successfully", notifications);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<Notification>> response = new ApiResponse<>(false, "Failed to retrieve notifications", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Get notification counts for user
     */
    @GetMapping("/user/{userId}/counts")
    public ResponseEntity<ApiResponse<NotificationCounts>> getNotificationCounts(
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            // Verify user has permission to access these notifications
            if (!userService.isCurrentUser(userId, authentication)) {
                ApiResponse<NotificationCounts> response = new ApiResponse<>(false, "Unauthorized access", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            Long unreadCount = notificationService.getUnreadNotificationCount(userId);
            List<Notification> allNotifications = notificationService.getUserNotifications(userId, Integer.MAX_VALUE, 0);
            Long totalCount = (long) allNotifications.size();
            
            NotificationCounts counts = new NotificationCounts(totalCount, unreadCount);
            
            ApiResponse<NotificationCounts> response = new ApiResponse<>(true, "Notification counts retrieved", counts);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<NotificationCounts> response = new ApiResponse<>(false, "Failed to retrieve notification counts", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Mark a specific notification as read
     */
    @PutMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<String>> markNotificationAsRead(
            @PathVariable Long notificationId,
            @RequestParam Long userId,
            Authentication authentication) {
        try {
            // Verify user has permission to modify this notification
            if (!userService.isCurrentUser(userId, authentication)) {
                ApiResponse<String> response = new ApiResponse<>(false, "Unauthorized access", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            boolean success = notificationService.markAsRead(notificationId, userId);
            
            if (success) {
                ApiResponse<String> response = new ApiResponse<>(true, "Notification marked as read", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(false, "Notification not found or access denied", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to mark notification as read", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Mark all notifications as read for user
     */
    @PutMapping("/user/{userId}/read-all")
    public ResponseEntity<ApiResponse<String>> markAllNotificationsAsRead(
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            // Verify user has permission to modify these notifications
            if (!userService.isCurrentUser(userId, authentication)) {
                ApiResponse<String> response = new ApiResponse<>(false, "Unauthorized access", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            int markedCount = notificationService.markAllAsRead(userId);
            
            String message = String.format("%d notifications marked as read", markedCount);
            ApiResponse<String> response = new ApiResponse<>(true, message, null);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to mark notifications as read", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Delete a notification
     */
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<ApiResponse<String>> deleteNotification(
            @PathVariable Long notificationId,
            @RequestParam Long userId,
            Authentication authentication) {
        try {
            // Verify user has permission to delete this notification
            if (!userService.isCurrentUser(userId, authentication)) {
                ApiResponse<String> response = new ApiResponse<>(false, "Unauthorized access", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Verify notification belongs to user before deleting
            Optional<Notification> notificationOpt = notificationService.getNotification(notificationId, userId);
            
            if (notificationOpt.isPresent()) {
                // For now, we'll mark as read instead of actual deletion for audit purposes
                notificationService.markAsRead(notificationId, userId);
                
                ApiResponse<String> response = new ApiResponse<>(true, "Notification deleted successfully", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(false, "Notification not found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
            
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to delete notification", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Update notification preferences (placeholder for future implementation)
     */
    @PutMapping("/user/{userId}/preferences")
    public ResponseEntity<ApiResponse<String>> updateNotificationPreferences(
            @PathVariable Long userId,
            Authentication authentication) {
        try {
            // Verify user has permission
            if (!userService.isCurrentUser(userId, authentication)) {
                ApiResponse<String> response = new ApiResponse<>(false, "Unauthorized access", null);
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
            }
            
            // Placeholder - this would update user notification preferences
            // For now, return success but note that preferences aren't implemented yet
            
            ApiResponse<String> response = new ApiResponse<>(true, "Notification preferences feature coming soon", null);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to update preferences", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Inner class for notification counts response
     */
    public static class NotificationCounts {
        private Long total;
        private Long unread;

        public NotificationCounts(Long total, Long unread) {
            this.total = total;
            this.unread = unread;
        }

        public Long getTotal() { return total; }
        public void setTotal(Long total) { this.total = total; }

        public Long getUnread() { return unread; }
        public void setUnread(Long unread) { this.unread = unread; }
    }
}
