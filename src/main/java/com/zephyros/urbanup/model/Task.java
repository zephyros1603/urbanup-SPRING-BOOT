package com.zephyros.urbanup.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "tasks")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Task {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    @NotBlank(message = "Task title is required")
    @Size(max = 200, message = "Title cannot exceed 200 characters")
    private String title;
    
    @Column(columnDefinition = "TEXT")
    @NotBlank(message = "Task description is required")
    @Size(max = 2000, message = "Description cannot exceed 2000 characters")
    private String description;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskCategory category;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status = TaskStatus.OPEN;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PricingType pricingType;
    
    @Column(precision = 10, scale = 2, nullable = false)
    @DecimalMin(value = "0.0", message = "Price must be positive")
    private BigDecimal price;
    
    // Location fields
    @Column(nullable = false)
    @NotBlank(message = "Location is required")
    private String location;
    
    @Column(name = "latitude")
    private Double latitude;
    
    @Column(name = "longitude")
    private Double longitude;
    
    @Column(name = "address_details")
    private String addressDetails;
    
    // Task timing
    @Column(name = "deadline")
    private LocalDateTime deadline;
    
    @Column(name = "estimated_duration_hours")
    private Integer estimatedDurationHours;
    
    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "poster_id", nullable = false)
    private User poster;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fulfiller_id")
    private User fulfiller;
    
    // Task media
    @ElementCollection
    @CollectionTable(name = "task_images", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "image_url")
    @JsonIgnore
    private List<String> imageUrls = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "task_files", joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "file_url")
    @JsonIgnore
    private List<String> fileUrls = new ArrayList<>();
    
    // Task progression timestamps
    @Column(name = "accepted_at")
    private LocalDateTime acceptedAt;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;
    
    // Additional fields
    @Column(name = "is_urgent")
    private Boolean isUrgent = false;
    
    @Column(name = "requires_verification")
    private Boolean requiresVerification = false;
    
    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;
    
    // Audit fields
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Enums
    public enum TaskCategory {
        PERSONAL_ERRANDS("Personal Errands"),
        PROFESSIONAL_TASKS("Professional Tasks"),
        HOUSEHOLD_HELP("Household Help"),
        MICRO_GIGS("Micro Gigs"),
        DELIVERY("Delivery"),
        CLEANING("Cleaning"),
        REPAIR_MAINTENANCE("Repair & Maintenance"),
        SHOPPING("Shopping"),
        ADMINISTRATIVE("Administrative"),
        OTHER("Other");
        
        private final String displayName;
        
        TaskCategory(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum TaskStatus {
        OPEN("Open"),
        ACCEPTED("Accepted"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        CONFIRMED("Confirmed"),
        CANCELLED("Cancelled"),
        DISPUTED("Disputed");
        
        private final String displayName;
        
        TaskStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum PricingType {
        FIXED("Fixed Price"),
        HOURLY("Hourly Rate");
        
        private final String displayName;
        
        PricingType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public Task() {}
    
    public Task(String title, String description, TaskCategory category, 
                PricingType pricingType, BigDecimal price, String location, User poster) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.pricingType = pricingType;
        this.price = price;
        this.location = location;
        this.poster = poster;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public TaskCategory getCategory() { return category; }
    public void setCategory(TaskCategory category) { this.category = category; }
    
    public TaskStatus getStatus() { return status; }
    public void setStatus(TaskStatus status) { this.status = status; }
    
    public PricingType getPricingType() { return pricingType; }
    public void setPricingType(PricingType pricingType) { this.pricingType = pricingType; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    
    public String getAddressDetails() { return addressDetails; }
    public void setAddressDetails(String addressDetails) { this.addressDetails = addressDetails; }
    
    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    
    public Integer getEstimatedDurationHours() { return estimatedDurationHours; }
    public void setEstimatedDurationHours(Integer estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
    
    public User getPoster() { return poster; }
    public void setPoster(User poster) { this.poster = poster; }
    
    public User getFulfiller() { return fulfiller; }
    public void setFulfiller(User fulfiller) { this.fulfiller = fulfiller; }
    
    public List<String> getImageUrls() { return imageUrls; }
    public void setImageUrls(List<String> imageUrls) { this.imageUrls = imageUrls; }
    
    public List<String> getFileUrls() { return fileUrls; }
    public void setFileUrls(List<String> fileUrls) { this.fileUrls = fileUrls; }
    
    public LocalDateTime getAcceptedAt() { return acceptedAt; }
    public void setAcceptedAt(LocalDateTime acceptedAt) { this.acceptedAt = acceptedAt; }
    
    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }
    
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    
    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }
    
    public Boolean getIsUrgent() { return isUrgent; }
    public void setIsUrgent(Boolean isUrgent) { this.isUrgent = isUrgent; }
    
    public Boolean getRequiresVerification() { return requiresVerification; }
    public void setRequiresVerification(Boolean requiresVerification) { this.requiresVerification = requiresVerification; }
    
    public String getSpecialInstructions() { return specialInstructions; }
    public void setSpecialInstructions(String specialInstructions) { this.specialInstructions = specialInstructions; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isAvailable() {
        return status == TaskStatus.OPEN;
    }
    
    public boolean isInProgress() {
        return status == TaskStatus.IN_PROGRESS || status == TaskStatus.ACCEPTED;
    }
    
    public boolean isCompleted() {
        return status == TaskStatus.COMPLETED || status == TaskStatus.CONFIRMED;
    }
    
    public void addImageUrl(String imageUrl) {
        if (this.imageUrls == null) {
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.add(imageUrl);
    }
    
    public void addFileUrl(String fileUrl) {
        if (this.fileUrls == null) {
            this.fileUrls = new ArrayList<>();
        }
        this.fileUrls.add(fileUrl);
    }
    
    @PreUpdate
    public void setLastModifiedDate() {
        this.updatedAt = LocalDateTime.now();
    }
}
