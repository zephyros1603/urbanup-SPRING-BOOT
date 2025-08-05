package com.urbanup.service;

import com.urbanup.entity.Task;
import com.urbanup.entity.TaskApplication;
import com.urbanup.exception.ResourceNotFoundException;
import com.urbanup.repository.TaskRepository;
import com.urbanup.repository.TaskApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final TaskApplicationRepository taskApplicationRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, TaskApplicationRepository taskApplicationRepository) {
        this.taskRepository = taskRepository;
        this.taskApplicationRepository = taskApplicationRepository;
    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public Task getTaskById(Long taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new ResourceNotFoundException("Task not found with id " + taskId));
    }

    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Task updateTask(Long taskId, Task taskDetails) {
        Task task = getTaskById(taskId);
        task.setTitle(taskDetails.getTitle());
        task.setDescription(taskDetails.getDescription());
        task.setDeadline(taskDetails.getDeadline());
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId) {
        Task task = getTaskById(taskId);
        taskRepository.delete(task);
    }

    public TaskApplication applyForTask(Long taskId, TaskApplication application) {
        application.setTask(getTaskById(taskId));
        return taskApplicationRepository.save(application);
    }

    public List<TaskApplication> getApplicationsForTask(Long taskId) {
        return taskApplicationRepository.findByTaskId(taskId);
    }
}