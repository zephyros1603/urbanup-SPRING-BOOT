package com.urbanup.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.urbanup.repository.TaskRepository;
import com.urbanup.repository.UserRepository;
import com.urbanup.dto.response.TaskResponse;
import com.urbanup.dto.response.UserStatsResponse;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SearchService {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    @Autowired
    public SearchService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public List<TaskResponse> searchTasks(String keyword) {
        return taskRepository.findByTitleContainingIgnoreCase(keyword)
                .stream()
                .map(task -> new TaskResponse(task.getId(), task.getTitle(), task.getDescription(), task.getCreatedAt()))
                .collect(Collectors.toList());
    }

    public List<UserStatsResponse> searchUsers(String keyword) {
        return userRepository.findByUsernameContainingIgnoreCase(keyword)
                .stream()
                .map(user -> new UserStatsResponse(user.getId(), user.getUsername(), user.getProfilePicture()))
                .collect(Collectors.toList());
    }
}