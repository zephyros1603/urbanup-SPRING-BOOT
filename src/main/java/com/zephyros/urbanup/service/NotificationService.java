package com.zephyros.urbanup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zephyros.urbanup.model.Chat;
import com.zephyros.urbanup.model.Notification;
import com.zephyros.urbanup.model.Payment;
import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.repository.NotificationRepository;
import com.zephyros.urbanup.repository.UserRepository;

@Service
@Transactional
public class NotificationService {
    
    @Autowired
    private NotificationRepository notificationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Send welcome notification to new user
     */
    public Notification sendWelcomeNotification(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.SYSTEM_ANNOUNCEMENT);
        notification.setPriority(Notification.NotificationPriority.LOW);
        notification.setTitle("Welcome to UrbanUP!");
        notification.setMessage("Welcome to UrbanUP! Complete your profile to start posting or fulfilling tasks.");
        notification.setDeepLinkUrl("/profile/complete");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send email verification success notification
     */
    public Notification sendEmailVerificationSuccessNotification(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.PROFILE_UPDATE);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        notification.setTitle("Email Verified!");
        notification.setMessage("Your email address has been successfully verified.");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send phone verification success notification
     */
    public Notification sendPhoneVerificationSuccessNotification(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.PROFILE_UPDATE);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        notification.setTitle("Phone Verified!");
        notification.setMessage("Your phone number has been successfully verified.");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send password change notification
     */
    public Notification sendPasswordChangeNotification(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.PROFILE_UPDATE);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setTitle("Password Changed");
        notification.setMessage("Your password has been successfully changed.");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send account deactivation notification
     */
    public Notification sendAccountDeactivationNotification(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.PROFILE_UPDATE);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setTitle("Account Deactivated");
        notification.setMessage("Your account has been deactivated. You can reactivate it anytime by logging in.");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send account reactivation notification
     */
    public Notification sendAccountReactivationNotification(User user) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.PROFILE_UPDATE);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        notification.setTitle("Account Reactivated");
        notification.setMessage("Welcome back! Your account has been reactivated.");
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send new task posted notification
     */
    public Notification sendTaskPostedNotification(User user, Task task) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.TASK_CREATED);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        notification.setTitle("Task Posted Successfully");
        notification.setMessage("Your task '" + task.getTitle() + "' has been posted and is now live.");
        notification.setDeepLinkUrl("/tasks/" + task.getId());
        notification.setTaskId(task.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send task application notification
     */
    public Notification sendTaskApplicationNotification(User taskPoster, Task task, User applicant) {
        Notification notification = new Notification();
        notification.setUser(taskPoster);
        notification.setType(Notification.NotificationType.TASK_STARTED);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setTitle("New Task Application");
        notification.setMessage(applicant.getFirstName() + " " + applicant.getLastName() + 
                              " has applied for your task '" + task.getTitle() + "'.");
        notification.setDeepLinkUrl("/tasks/" + task.getId() + "/applications");
        notification.setTaskId(task.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send application accepted notification
     */
    public Notification sendApplicationAcceptedNotification(User applicant, Task task) {
        Notification notification = new Notification();
        notification.setUser(applicant);
        notification.setType(Notification.NotificationType.TASK_ACCEPTED);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setTitle("Application Accepted!");
        notification.setMessage("Congratulations! Your application for '" + task.getTitle() + "' has been accepted.");
        notification.setDeepLinkUrl("/tasks/" + task.getId());
        notification.setTaskId(task.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send application rejected notification
     */
    public Notification sendApplicationRejectedNotification(User applicant, Task task) {
        Notification notification = new Notification();
        notification.setUser(applicant);
        notification.setType(Notification.NotificationType.TASK_CANCELLED);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        notification.setTitle("Application Update");
        notification.setMessage("Your application for '" + task.getTitle() + "' was not selected this time.");
        notification.setDeepLinkUrl("/tasks/search");
        notification.setTaskId(task.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send task completed notification
     */
    public Notification sendTaskCompletedNotification(User user, Task task) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.TASK_COMPLETED);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setTitle("Task Completed");
        notification.setMessage("The task '" + task.getTitle() + "' has been marked as completed.");
        notification.setDeepLinkUrl("/tasks/" + task.getId());
        notification.setTaskId(task.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send payment notification
     */
    public Notification sendPaymentNotification(User user, Payment payment, String messageText) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.PAYMENT_RECEIVED);
        notification.setPriority(Notification.NotificationPriority.HIGH);
        notification.setTitle("Payment Update");
        notification.setMessage(messageText);
        notification.setDeepLinkUrl("/payments/" + payment.getId());
        notification.setPaymentId(payment.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send new message notification
     */
    public Notification sendNewMessageNotification(User recipient, Chat chat, String senderName) {
        Notification notification = new Notification();
        notification.setUser(recipient);
        notification.setType(Notification.NotificationType.NEW_MESSAGE);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        notification.setTitle("New Message");
        notification.setMessage("You have a new message from " + senderName + 
                              " about '" + chat.getTask().getTitle() + "'.");
        notification.setDeepLinkUrl("/chats/" + chat.getId());
        notification.setChatId(chat.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Send review request notification
     */
    public Notification sendReviewRequestNotification(User user, Task task) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(Notification.NotificationType.REVIEW_RECEIVED);
        notification.setPriority(Notification.NotificationPriority.NORMAL);
        notification.setTitle("Review Request");
        notification.setMessage("Please leave a review for the completed task '" + task.getTitle() + "'.");
        notification.setDeepLinkUrl("/tasks/" + task.getId() + "/review");
        notification.setTaskId(task.getId());
        notification.setCreatedAt(LocalDateTime.now());
        notification.setIsRead(false);
        
        return notificationRepository.save(notification);
    }
    
    /**
     * Mark notification as read
     */
    public boolean markAsRead(Long notificationId, Long userId) {
        return notificationRepository.findById(notificationId)
                .filter(notification -> notification.getUser().getId().equals(userId))
                .map(notification -> {
                    notification.setIsRead(true);
                    notification.setReadAt(LocalDateTime.now());
                    notificationRepository.save(notification);
                    return true;
                })
                .orElse(false);
    }
    
    /**
     * Mark all notifications as read for a user
     */
    public int markAllAsRead(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            List<Notification> unreadNotifications = notificationRepository.findUnreadNotificationsForUser(user);
            
            for (Notification notification : unreadNotifications) {
                notification.setIsRead(true);
                notification.setReadAt(LocalDateTime.now());
            }
            
            notificationRepository.saveAll(unreadNotifications);
            return unreadNotifications.size();
        }
        return 0;
    }
    
    /**
     * Get user notifications with pagination
     */
    @Transactional(readOnly = true)
    public List<Notification> getUserNotifications(Long userId, int limit, int offset) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return notificationRepository.findByUserOrderByCreatedAtDesc(userOpt.get())
                    .stream()
                    .skip(offset)
                    .limit(limit)
                    .toList();
        }
        return List.of();
    }
    
    /**
     * Get unread notification count
     */
    @Transactional(readOnly = true)
    public Long getUnreadNotificationCount(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(user -> notificationRepository.countUnreadNotificationsForUser(user)).orElse(0L);
    }
    
    /**
     * Delete old notifications (older than specified days)
     */
    public int deleteOldNotifications(int daysOld) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        List<Notification> oldNotifications = notificationRepository.findRecentNotifications(cutoffDate);
        
        notificationRepository.deleteAll(oldNotifications);
        return oldNotifications.size();
    }
    
    /**
     * Get notification by ID and user ID
     */
    @Transactional(readOnly = true)
    public Optional<Notification> getNotification(Long notificationId, Long userId) {
        return notificationRepository.findById(notificationId)
                .filter(notification -> notification.getUser().getId().equals(userId));
    }
}
