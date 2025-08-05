package com.zephyros.urbanup.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zephyros.urbanup.dto.ApiResponse;
import com.zephyros.urbanup.dto.TaskApplicationDto;
import com.zephyros.urbanup.dto.TaskCreateDto;
import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.TaskApplication;
import com.zephyros.urbanup.repository.TaskRepository;
import com.zephyros.urbanup.service.TaskService;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    
    @Autowired
    private TaskService taskService;
    
    @Autowired
    private TaskRepository taskRepository;
    
    /**
     * Create a new task
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Task>> createTask(@RequestBody TaskCreateDto taskDto) {
        try {
            Task task = taskService.createTask(
                taskDto.getPosterId(),
                taskDto.getTitle(),
                taskDto.getDescription(),
                taskDto.getPrice(),
                Task.PricingType.FIXED, // Default pricing type
                taskDto.getLocation(),
                null, // latitude
                null, // longitude
                taskDto.getDeadline(),
                taskDto.getCategory()
            );
            
            ApiResponse<Task> response = new ApiResponse<>(true, "Task created successfully", task);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<Task> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<Task> response = new ApiResponse<>(false, "Task creation failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get task by ID
     */
    @GetMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Task>> getTask(@PathVariable Long taskId) {
        try {
            Optional<Task> taskOpt = taskService.getTaskById(taskId);
            
            if (taskOpt.isPresent()) {
                ApiResponse<Task> response = new ApiResponse<>(true, "Task found", taskOpt.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            ApiResponse<Task> response = new ApiResponse<>(false, "Failed to retrieve task", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update task
     */
    @PutMapping("/{taskId}")
    public ResponseEntity<ApiResponse<Task>> updateTask(
            @PathVariable Long taskId,
            @RequestBody TaskCreateDto taskDto) {
        try {
            Task updatedTask = taskService.updateTask(
                taskId,
                taskDto.getPosterId(),
                taskDto.getTitle(),
                taskDto.getDescription(),
                taskDto.getPrice(),
                Task.PricingType.FIXED, // Default pricing type
                taskDto.getLocation(),
                null, // latitude
                null, // longitude
                taskDto.getDeadline(),
                taskDto.getCategory()
            );
            
            ApiResponse<Task> response = new ApiResponse<>(true, "Task updated successfully", updatedTask);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<Task> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<Task> response = new ApiResponse<>(false, "Task update failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Apply for a task
     */
    @PostMapping("/{taskId}/apply")
    public ResponseEntity<ApiResponse<TaskApplication>> applyForTask(
            @PathVariable Long taskId,
            @RequestBody TaskApplicationDto applicationDto) {
        try {
            TaskApplication application = taskService.applyForTask(
                taskId,
                applicationDto.getFulfillerId(),
                applicationDto.getMessage(),
                applicationDto.getProposedPrice().doubleValue()
            );
            
            ApiResponse<TaskApplication> response = new ApiResponse<>(true, "Application submitted successfully", application);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<TaskApplication> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<TaskApplication> response = new ApiResponse<>(false, "Application failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Accept an application
     */
    @PutMapping("/{taskId}/applications/{applicationId}/accept")
    public ResponseEntity<ApiResponse<String>> acceptApplication(
            @PathVariable Long taskId,
            @PathVariable Long applicationId,
            @RequestParam Long posterId) {
        try {
            boolean accepted = taskService.acceptApplication(taskId, applicationId, posterId);
            
            if (accepted) {
                ApiResponse<String> response = new ApiResponse<>(true, "Application accepted successfully", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(false, "Failed to accept application", null);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to accept application", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Mark task as completed
     */
    @PutMapping("/{taskId}/complete")
    public ResponseEntity<ApiResponse<String>> completeTask(
            @PathVariable Long taskId,
            @RequestParam Long fulfillerId) {
        try {
            boolean completed = taskService.markTaskCompleted(taskId, fulfillerId);
            
            if (completed) {
                ApiResponse<String> response = new ApiResponse<>(true, "Task completed successfully", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(false, "Failed to complete task", null);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to complete task", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Confirm task completion
     */
    @PutMapping("/{taskId}/confirm")
    public ResponseEntity<ApiResponse<String>> confirmTaskCompletion(
            @PathVariable Long taskId,
            @RequestParam Long posterId) {
        try {
            boolean confirmed = taskService.confirmTaskCompletion(taskId, posterId);
            
            if (confirmed) {
                ApiResponse<String> response = new ApiResponse<>(true, "Task completion confirmed", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(false, "Failed to confirm completion", null);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Failed to confirm completion", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get all available tasks
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Task>>> getAllTasks() {
        try {
            // Use repository method with eager fetching to avoid lazy loading issues
            List<Task> tasks = taskRepository.findAllWithUsersEager();
            
            ApiResponse<List<Task>> response = new ApiResponse<>(true, "Tasks retrieved successfully", tasks);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<Task>> response = new ApiResponse<>(false, "Failed to retrieve tasks", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get tasks by poster
     */
    @GetMapping("/poster/{posterId}")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByPoster(@PathVariable Long posterId) {
        try {
            List<Task> tasks = taskService.getTasksByPoster(posterId);
            
            ApiResponse<List<Task>> response = new ApiResponse<>(true, "Poster tasks retrieved", tasks);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<Task>> response = new ApiResponse<>(false, "Failed to retrieve poster tasks", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get tasks by fulfiller
     */
    @GetMapping("/fulfiller/{fulfillerId}")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByFulfiller(@PathVariable Long fulfillerId) {
        try {
            List<Task> tasks = taskService.getTasksByFulfiller(fulfillerId);
            
            ApiResponse<List<Task>> response = new ApiResponse<>(true, "Fulfiller tasks retrieved", tasks);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<Task>> response = new ApiResponse<>(false, "Failed to retrieve fulfiller tasks", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Search tasks
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Task>>> searchTasks(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Task.TaskCategory category,
            @RequestParam(required = false) Task.TaskStatus status,
            @RequestParam(defaultValue = "20") int limit,
            @RequestParam(defaultValue = "0") int offset) {
        try {
            List<Task> tasks = taskService.searchTasks(keyword, category, status, limit, offset);
            
            ApiResponse<List<Task>> response = new ApiResponse<>(true, "Search completed", tasks);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<Task>> response = new ApiResponse<>(false, "Search failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get task applications
     */
    @GetMapping("/{taskId}/applications")
    public ResponseEntity<ApiResponse<List<TaskApplication>>> getTaskApplications(
            @PathVariable Long taskId,
            @RequestParam Long posterId) {
        try {
            List<TaskApplication> applications = taskService.getApplicationsForTask(taskId, posterId);
            
            ApiResponse<List<TaskApplication>> response = new ApiResponse<>(true, "Applications retrieved", applications);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<TaskApplication>> response = new ApiResponse<>(false, "Failed to retrieve applications", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get tasks by category
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<Task>>> getTasksByCategory(@PathVariable Task.TaskCategory category) {
        try {
            // Use the search method with category filter
            List<Task> tasks = taskService.searchTasks(null, category, null, 50, 0);
            
            ApiResponse<List<Task>> response = new ApiResponse<>(true, "Category tasks retrieved", tasks);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<Task>> response = new ApiResponse<>(false, "Failed to retrieve category tasks", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get urgent tasks
     */
    @GetMapping("/urgent")
    public ResponseEntity<ApiResponse<List<Task>>> getUrgentTasks() {
        try {
            // For now, return empty list as this method isn't implemented in service
            // TODO: Implement getUrgentTasks in TaskService
            List<Task> tasks = List.of();
            
            ApiResponse<List<Task>> response = new ApiResponse<>(true, "Urgent tasks retrieved", tasks);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<Task>> response = new ApiResponse<>(false, "Failed to retrieve urgent tasks", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
