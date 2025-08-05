package com.zephyros.urbanup.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class TaskApplicationDto {
    
    @NotNull(message = "Fulfiller ID is required")
    private Long fulfillerId;
    
    @NotNull(message = "Proposed price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Proposed price must be greater than 0")
    private BigDecimal proposedPrice;
    
    private String message;
    
    public TaskApplicationDto() {}
    
    public TaskApplicationDto(Long fulfillerId, BigDecimal proposedPrice, String message) {
        this.fulfillerId = fulfillerId;
        this.proposedPrice = proposedPrice;
        this.message = message;
    }
    
    // Getters and setters
    public Long getFulfillerId() { return fulfillerId; }
    public void setFulfillerId(Long fulfillerId) { this.fulfillerId = fulfillerId; }
    
    public BigDecimal getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(BigDecimal proposedPrice) { this.proposedPrice = proposedPrice; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
