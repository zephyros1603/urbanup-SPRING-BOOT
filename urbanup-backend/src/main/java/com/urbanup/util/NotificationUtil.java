package com.urbanup.util;

import com.urbanup.entity.Notification;
import com.urbanup.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotificationUtil {

    private final NotificationService notificationService;

    @Autowired
    public NotificationUtil(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public void sendNotification(Notification notification) {
        // Logic to send notification
        notificationService.save(notification);
    }

    public void sendUserNotification(Long userId, String message) {
        Notification notification = new Notification();
        notification.setUserId(userId);
        notification.setMessage(message);
        sendNotification(notification);
    }

    public void sendTaskNotification(Long taskId, String message) {
        Notification notification = new Notification();
        notification.setTaskId(taskId);
        notification.setMessage(message);
        sendNotification(notification);
    }
}