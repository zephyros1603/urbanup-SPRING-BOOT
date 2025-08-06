package com.urbanup.dto.response;

public class UserStatsResponse {
    private Long userId;
    private String username;
    private int completedTasks;
    private int pendingTasks;
    private int totalReviews;
    private double averageRating;

    public UserStatsResponse(Long userId, String username, int completedTasks, int pendingTasks, int totalReviews, double averageRating) {
        this.userId = userId;
        this.username = username;
        this.completedTasks = completedTasks;
        this.pendingTasks = pendingTasks;
        this.totalReviews = totalReviews;
        this.averageRating = averageRating;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public int getCompletedTasks() {
        return completedTasks;
    }

    public int getPendingTasks() {
        return pendingTasks;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public double getAverageRating() {
        return averageRating;
    }
}