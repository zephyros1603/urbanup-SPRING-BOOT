package com.zephyros.urbanup.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "messages")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "chat"})
public class Message {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "chat_id", nullable = false)
    private Chat chat;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "sender_id", nullable = true)
    private User sender;
    
    @Column(columnDefinition = "TEXT")
    @Size(max = 1000, message = "Message cannot exceed 1000 characters")
    private String content;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MessageType messageType = MessageType.TEXT;
    
    // Media attachments
    @ElementCollection
    @CollectionTable(name = "message_attachments", joinColumns = @JoinColumn(name = "message_id"))
    @Column(name = "attachment_url")
    private List<String> attachmentUrls = new ArrayList<>();
    
    @Column(name = "is_read")
    private Boolean isRead = false;
    
    @Column(name = "is_system_message")
    private Boolean isSystemMessage = false;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "read_at")
    private LocalDateTime readAt;
    
    // For system messages and status updates
    @Column(name = "system_message_data", columnDefinition = "TEXT")
    private String systemMessageData;
    
    public enum MessageType {
        TEXT("Text"),
        IMAGE("Image"),
        FILE("File"),
        LOCATION("Location"),
        SYSTEM("System Message"),
        STATUS_UPDATE("Status Update");
        
        private final String displayName;
        
        MessageType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public Message() {}
    
    public Message(Chat chat, User sender, String content, MessageType messageType) {
        this.chat = chat;
        this.sender = sender;
        this.content = content;
        this.messageType = messageType;
    }
    
    // Static factory methods for different message types
    public static Message createTextMessage(Chat chat, User sender, String content) {
        return new Message(chat, sender, content, MessageType.TEXT);
    }
    
    public static Message createSystemMessage(Chat chat, String content, String systemData) {
        Message message = new Message();
        message.setChat(chat);
        message.setContent(content);
        message.setMessageType(MessageType.SYSTEM);
        message.setIsSystemMessage(true);
        message.setSystemMessageData(systemData);
        return message;
    }
    
    public static Message createStatusUpdate(Chat chat, String content) {
        Message message = new Message();
        message.setChat(chat);
        message.setContent(content);
        message.setMessageType(MessageType.STATUS_UPDATE);
        message.setIsSystemMessage(true);
        return message;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Chat getChat() { return chat; }
    public void setChat(Chat chat) { this.chat = chat; }
    
    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public MessageType getMessageType() { return messageType; }
    public void setMessageType(MessageType messageType) { this.messageType = messageType; }
    
    public List<String> getAttachmentUrls() { return attachmentUrls; }
    public void setAttachmentUrls(List<String> attachmentUrls) { this.attachmentUrls = attachmentUrls; }
    
    public Boolean getIsRead() { return isRead; }
    public void setIsRead(Boolean isRead) { this.isRead = isRead; }
    
    public Boolean getIsSystemMessage() { return isSystemMessage; }
    public void setIsSystemMessage(Boolean isSystemMessage) { this.isSystemMessage = isSystemMessage; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getReadAt() { return readAt; }
    public void setReadAt(LocalDateTime readAt) { this.readAt = readAt; }
    
    public String getSystemMessageData() { return systemMessageData; }
    public void setSystemMessageData(String systemMessageData) { this.systemMessageData = systemMessageData; }
    
    // Helper methods
    public void addAttachment(String attachmentUrl) {
        if (this.attachmentUrls == null) {
            this.attachmentUrls = new ArrayList<>();
        }
        this.attachmentUrls.add(attachmentUrl);
    }
    
    public void markAsRead() {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
    }
    
    public boolean hasAttachments() {
        return attachmentUrls != null && !attachmentUrls.isEmpty();
    }
}
