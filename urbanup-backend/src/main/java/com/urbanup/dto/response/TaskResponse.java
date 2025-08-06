package com.urbanup.dto.response;

import java.time.LocalDateTime;

public class TaskResponse {
    private Long id;
    private String title;
    private String description;
    private String status;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
    private Long creatorId;
    private Long fulfillersCount;
    private Long applicationsCount;

    public TaskResponse(Long id, String title, String description, String status, LocalDateTime createdDate, LocalDateTime updatedDate, Long creatorId, Long fulfillersCount, Long applicationsCount) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.createdDate = createdDate;
        this.updatedDate = updatedDate;
        this.creatorId = creatorId;
        this.fulfillersCount = fulfillersCount;
        this.applicationsCount = applicationsCount;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    public LocalDateTime getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(LocalDateTime updatedDate) {
        this.updatedDate = updatedDate;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Long getFulfillersCount() {
        return fulfillersCount;
    }

    public void setFulfillersCount(Long fulfillersCount) {
        this.fulfillersCount = fulfillersCount;
    }

    public Long getApplicationsCount() {
        return applicationsCount;
    }

    public void setApplicationsCount(Long applicationsCount) {
        this.applicationsCount = applicationsCount;
    }
}