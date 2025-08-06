package com.zephyros.urbanup.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChatResponseDto {
    private Long id;
    private String taskTitle;
    private Long otherParticipantId;
    private String otherParticipantName;
    private List<MessageResponseDto> messages;
    private LocalDateTime lastActivity;

    // Constructors
    public ChatResponseDto() {}

    public ChatResponseDto(Long id, String taskTitle, Long otherParticipantId, String otherParticipantName, List<MessageResponseDto> messages, LocalDateTime lastActivity) {
        this.id = id;
        this.taskTitle = taskTitle;
        this.otherParticipantId = otherParticipantId;
        this.otherParticipantName = otherParticipantName;
        this.messages = messages;
        this.lastActivity = lastActivity;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskTitle() {
        return taskTitle;
    }

    public void setTaskTitle(String taskTitle) {
        this.taskTitle = taskTitle;
    }

    public Long getOtherParticipantId() {
        return otherParticipantId;
    }

    public void setOtherParticipantId(Long otherParticipantId) {
        this.otherParticipantId = otherParticipantId;
    }

    public String getOtherParticipantName() {
        return otherParticipantName;
    }

    public void setOtherParticipantName(String otherParticipantName) {
        this.otherParticipantName = otherParticipantName;
    }

    public List<MessageResponseDto> getMessages() {
        return messages;
    }

    public void setMessages(List<MessageResponseDto> messages) {
        this.messages = messages;
    }

    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }
}
