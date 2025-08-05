package com.urbanup.dto.request;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ReviewCreateRequest {

    @NotNull(message = "Task ID cannot be null")
    private Long taskId;

    @NotBlank(message = "Review content cannot be empty")
    private String content;

    @NotNull(message = "Rating cannot be null")
    private Integer rating;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }
}