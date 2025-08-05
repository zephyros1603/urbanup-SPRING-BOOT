package com.zephyros.urbanup.dto;

import com.zephyros.urbanup.model.Message;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class MessageSendDto {
    
    @NotNull(message = "Sender ID is required")
    private Long senderId;
    
    @NotBlank(message = "Content is required")
    private String content;
    
    private Message.MessageType messageType;
    
    public MessageSendDto() {}
    
    public MessageSendDto(Long senderId, String content, Message.MessageType messageType) {
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
    }
    
    // Getters and setters
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public Message.MessageType getMessageType() { return messageType; }
    public void setMessageType(Message.MessageType messageType) { this.messageType = messageType; }
}
