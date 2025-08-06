package com.zephyros.urbanup.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.zephyros.urbanup.model.Task;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class TaskCreateDto {
    
    @NotNull(message = "Poster ID is required")
    private Long posterId;
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "Description is required")
    private String description;
    
    @NotNull(message = "Category is required")
    private Task.TaskCategory category;
    
    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Price must be greater than 0")
    private BigDecimal price;
    
    @NotNull(message = "Pricing type is required")
    private Task.PricingType pricingType;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    private String cityArea;
    
    private String fullAddress;
    
    private LocalDateTime deadline;
    
    private Integer estimatedDurationHours;
    
    private Boolean isUrgent = false;
    
    private String specialRequirements;
    
    private List<String> skillsRequired;

    public TaskCreateDto() {}

    // Getters and setters
    public Long getPosterId() { return posterId; }
    public void setPosterId(Long posterId) { this.posterId = posterId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Task.TaskCategory getCategory() { return category; }
    public void setCategory(Task.TaskCategory category) { this.category = category; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Task.PricingType getPricingType() { return pricingType; }
    public void setPricingType(Task.PricingType pricingType) { this.pricingType = pricingType; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
    
    public String getCityArea() { return cityArea; }
    public void setCityArea(String cityArea) { this.cityArea = cityArea; }
    
    public String getFullAddress() { return fullAddress; }
    public void setFullAddress(String fullAddress) { this.fullAddress = fullAddress; }

    public LocalDateTime getDeadline() { return deadline; }
    public void setDeadline(LocalDateTime deadline) { this.deadline = deadline; }
    
    public Integer getEstimatedDurationHours() { return estimatedDurationHours; }
    public void setEstimatedDurationHours(Integer estimatedDurationHours) { this.estimatedDurationHours = estimatedDurationHours; }
    
    public Boolean getIsUrgent() { return isUrgent; }
    public void setIsUrgent(Boolean isUrgent) { this.isUrgent = isUrgent; }
    
    public String getSpecialRequirements() { return specialRequirements; }
    public void setSpecialRequirements(String specialRequirements) { this.specialRequirements = specialRequirements; }
    
    public List<String> getSkillsRequired() { return skillsRequired; }
    public void setSkillsRequired(List<String> skillsRequired) { this.skillsRequired = skillsRequired; }
}
