package com.zephyros.urbanup.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TaskApplicationDto {
    
    @NotNull(message = "Fulfiller ID is required")
    private Long fulfillerId;
    
    @NotNull(message = "Proposed rate is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Proposed rate must be greater than 0")
    @JsonProperty("proposedRate")
    private BigDecimal proposedPrice;
    
    private String message;
    
    @JsonProperty("estimatedCompletionTime")
    private LocalDateTime estimatedCompletionTime;
    
    public TaskApplicationDto() {}
    
    public TaskApplicationDto(Long fulfillerId, BigDecimal proposedPrice, String message) {
        this.fulfillerId = fulfillerId;
        this.proposedPrice = proposedPrice;
        this.message = message;
    }
    
    public TaskApplicationDto(Long fulfillerId, BigDecimal proposedPrice, String message, LocalDateTime estimatedCompletionTime) {
        this.fulfillerId = fulfillerId;
        this.proposedPrice = proposedPrice;
        this.message = message;
        this.estimatedCompletionTime = estimatedCompletionTime;
    }
    
    // Getters and setters
    public Long getFulfillerId() { return fulfillerId; }
    public void setFulfillerId(Long fulfillerId) { this.fulfillerId = fulfillerId; }
    
    public BigDecimal getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(BigDecimal proposedPrice) { this.proposedPrice = proposedPrice; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public LocalDateTime getEstimatedCompletionTime() { return estimatedCompletionTime; }
    public void setEstimatedCompletionTime(LocalDateTime estimatedCompletionTime) { this.estimatedCompletionTime = estimatedCompletionTime; }
}
