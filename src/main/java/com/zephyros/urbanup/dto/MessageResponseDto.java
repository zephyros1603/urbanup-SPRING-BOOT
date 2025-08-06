package com.zephyros.urbanup.dto;

import java.time.LocalDateTime;

public class MessageResponseDto {
    private Long id;
    private String content;
    private String messageType;
    private LocalDateTime createdAt;
    private String senderName;
    private Long senderId;
    private boolean isRead;
    
    public MessageResponseDto() {}
    
    public MessageResponseDto(Long id, String content, String messageType, 
                            LocalDateTime createdAt, String senderName, 
                            Long senderId, boolean isRead) {
        this.id = id;
        this.content = content;
        this.messageType = messageType;
        this.createdAt = createdAt;
        this.senderName = senderName;
        this.senderId = senderId;
        this.isRead = isRead;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getMessageType() { return messageType; }
    public void setMessageType(String messageType) { this.messageType = messageType; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public boolean isRead() { return isRead; }
    public void setRead(boolean read) { isRead = read; }
}
