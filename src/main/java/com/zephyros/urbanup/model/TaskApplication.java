package com.zephyros.urbanup.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
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

@Entity
@Table(name = "task_applications")
public class TaskApplication {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_id", nullable = false)
    private Task task;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "applicant_id", nullable = false)
    private User applicant;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ApplicationStatus status = ApplicationStatus.PENDING;
    
    @Column(columnDefinition = "TEXT")
    private String message; // Optional message from applicant
    
    @Column(name = "proposed_price")
    private Double proposedPrice; // If applicant wants to negotiate price
    
    @Column(name = "estimated_completion_time")
    private Integer estimatedCompletionTime; // In hours
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "responded_at")
    private LocalDateTime respondedAt;
    
    @Column(name = "response_message")
    private String responseMessage; // Message from task poster
    
    public enum ApplicationStatus {
        PENDING("Pending"),
        ACCEPTED("Accepted"),
        REJECTED("Rejected"),
        WITHDRAWN("Withdrawn");
        
        private final String displayName;
        
        ApplicationStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public TaskApplication() {}
    
    public TaskApplication(Task task, User applicant, String message) {
        this.task = task;
        this.applicant = applicant;
        this.message = message;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Task getTask() { return task; }
    public void setTask(Task task) { this.task = task; }
    
    public User getApplicant() { return applicant; }
    public void setApplicant(User applicant) { this.applicant = applicant; }
    
    public ApplicationStatus getStatus() { return status; }
    public void setStatus(ApplicationStatus status) { this.status = status; }
    
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public Double getProposedPrice() { return proposedPrice; }
    public void setProposedPrice(Double proposedPrice) { this.proposedPrice = proposedPrice; }
    
    public Integer getEstimatedCompletionTime() { return estimatedCompletionTime; }
    public void setEstimatedCompletionTime(Integer estimatedCompletionTime) { this.estimatedCompletionTime = estimatedCompletionTime; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getRespondedAt() { return respondedAt; }
    public void setRespondedAt(LocalDateTime respondedAt) { this.respondedAt = respondedAt; }
    
    public String getResponseMessage() { return responseMessage; }
    public void setResponseMessage(String responseMessage) { this.responseMessage = responseMessage; }
    
    // Helper methods
    public void accept(String responseMessage) {
        this.status = ApplicationStatus.ACCEPTED;
        this.responseMessage = responseMessage;
        this.respondedAt = LocalDateTime.now();
    }
    
    public void reject(String responseMessage) {
        this.status = ApplicationStatus.REJECTED;
        this.responseMessage = responseMessage;
        this.respondedAt = LocalDateTime.now();
    }
    
    public void withdraw() {
        this.status = ApplicationStatus.WITHDRAWN;
        this.respondedAt = LocalDateTime.now();
    }
    
    public boolean isPending() {
        return status == ApplicationStatus.PENDING;
    }
    
    public boolean isAccepted() {
        return status == ApplicationStatus.ACCEPTED;
    }
}
