package com.zephyros.urbanup.dto;

import java.time.LocalDateTime;

public class ChatDTO {
    private Long id;
    private Long taskId;
    private String taskTitle;
    private Long posterId;
    private String posterName;
    private Long fulfillerId;
    private String fulfillerName;
    private int messageCount;
    private int unreadCount;
    private String lastMessagePreview;
    private LocalDateTime lastActivity;
    private boolean isActive;
    
    // Getters and setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public String getTaskTitle() {
        return taskTitle;
    }
    
    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }
    
    public Long getPosterId() {
        return posterId;
    }
    
    public void setPosterId(Long posterId) {
        this.posterId = posterId;
    }
    
    public String getPosterName() {
        return posterName;
    }
    
    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }
    
    public Long getFulfillerId() {
        return fulfillerId;
    }
    
    public void setFulfillerId(Long fulfillerId) {
        this.fulfillerId = fulfillerId;
    }
    
    public String getFulfillerName() {
        return fulfillerName;
    }
    
    public void setFulfillerName(String fulfillerName) {
        this.fulfillerName = fulfillerName;
    }
    
    public int getMessageCount() {
        return messageCount;
    }
    
    public void setMessageCount(int messageCount) {
        this.messageCount = messageCount;
    }
    
    public int getUnreadCount() {
        return unreadCount;
    }
    
    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }
    
    public String getLastMessagePreview() {
        return lastMessagePreview;
    }
    
    public void setLastMessagePreview(String lastMessagePreview) {
        this.lastMessagePreview = lastMessagePreview;
    }
    
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }
    
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
    
    public boolean isActive() {
        return isActive;
    }
    
    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }
}
