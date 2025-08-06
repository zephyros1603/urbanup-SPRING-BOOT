package com.urbanup.dto.response;

import java.util.List;

public class PlatformStatsResponse {
    private int totalUsers;
    private int totalTasks;
    private int totalReviews;
    private int totalPayments;
    private List<String> topCategories;

    public PlatformStatsResponse(int totalUsers, int totalTasks, int totalReviews, int totalPayments, List<String> topCategories) {
        this.totalUsers = totalUsers;
        this.totalTasks = totalTasks;
        this.totalReviews = totalReviews;
        this.totalPayments = totalPayments;
        this.topCategories = topCategories;
    }

    public int getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(int totalUsers) {
        this.totalUsers = totalUsers;
    }

    public int getTotalTasks() {
        return totalTasks;
    }

    public void setTotalTasks(int totalTasks) {
        this.totalTasks = totalTasks;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }

    public int getTotalPayments() {
        return totalPayments;
    }

    public void setTotalPayments(int totalPayments) {
        this.totalPayments = totalPayments;
    }

    public List<String> getTopCategories() {
        return topCategories;
    }

    public void setTopCategories(List<String> topCategories) {
        this.topCategories = topCategories;
    }
}