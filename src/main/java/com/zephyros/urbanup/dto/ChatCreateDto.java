package com.zephyros.urbanup.dto;

import jakarta.validation.constraints.NotNull;

public class ChatCreateDto {
    
    @NotNull(message = "Task ID is required")
    private Long taskId;
    
    @NotNull(message = "Poster ID is required")
    private Long posterId;
    
    @NotNull(message = "Fulfiller ID is required")
    private Long fulfillerId;
    
    public ChatCreateDto() {}
    
    public ChatCreateDto(Long taskId, Long posterId, Long fulfillerId) {
        this.taskId = taskId;
        this.posterId = posterId;
        this.fulfillerId = fulfillerId;
    }
    
    // Getters and setters
    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }
    
    public Long getPosterId() { return posterId; }
    public void setPosterId(Long posterId) { this.posterId = posterId; }
    
    public Long getFulfillerId() { return fulfillerId; }
    public void setFulfillerId(Long fulfillerId) { this.fulfillerId = fulfillerId; }
}
