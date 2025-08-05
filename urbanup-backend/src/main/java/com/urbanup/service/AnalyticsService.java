package com.urbanup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.urbanup.repository.TaskRepository;
import com.urbanup.repository.UserRepository;
import com.urbanup.dto.response.PlatformStatsResponse;

@Service
public class AnalyticsService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    public PlatformStatsResponse getPlatformStatistics() {
        long totalUsers = userRepository.count();
        long totalTasks = taskRepository.count();
        // Additional statistics can be calculated here

        return new PlatformStatsResponse(totalUsers, totalTasks);
    }

    // Additional analytics methods can be added here
}