package com.zephyros.urbanup.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.TaskApplication;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.repository.TaskApplicationRepository;
import com.zephyros.urbanup.repository.TaskRepository;
import com.zephyros.urbanup.repository.UserRepository;

@Service
@Transactional
public class TaskService {
    
    @Autowired
    private TaskRepository taskRepository;
    
    @Autowired
    private TaskApplicationRepository taskApplicationRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    // Task Creation and Management
    
    /**
     * Create a new task
     */
    public Task createTask(Long posterId, String title, String description, 
                          BigDecimal price, Task.PricingType pricingType, 
                          String location, String cityArea, String fullAddress,
                          Double latitude, Double longitude,
                          LocalDateTime deadline, Integer estimatedDurationHours,
                          Boolean isUrgent, String specialRequirements,
                          List<String> skillsRequired, Task.TaskCategory category) {
        
        Optional<User> posterOpt = userRepository.findById(posterId);
        if (posterOpt.isEmpty()) {
            throw new IllegalArgumentException("Poster not found");
        }
        
        User poster = posterOpt.get();
        
        // Validate that user is verified to post tasks
        // Temporarily disabled for testing - all users can post tasks
        // if (!poster.isVerified()) {
        //     throw new IllegalArgumentException("User must be verified to post tasks");
        // }
        
        Task task = new Task();
        task.setPoster(poster);
        task.setTitle(title);
        task.setDescription(description);
        task.setPrice(price);
        task.setPricingType(pricingType);
        task.setLocation(location);
        task.setCityArea(cityArea);
        task.setFullAddress(fullAddress);
        task.setLatitude(latitude);
        task.setLongitude(longitude);
        task.setDeadline(deadline);
        task.setEstimatedDurationHours(estimatedDurationHours);
        task.setIsUrgent(isUrgent != null ? isUrgent : false);
        task.setSpecialRequirements(specialRequirements);
        task.setSkillsRequired(skillsRequired);
        task.setCategory(category);
        task.setStatus(Task.TaskStatus.OPEN);
        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        Task savedTask = taskRepository.save(task);
        
        // Send notification to poster
        notificationService.sendTaskPostedNotification(poster, savedTask);
        
        return savedTask;
    }
    
    /**
     * Update task details (only allowed if task is OPEN)
     */
    public Task updateTask(Long taskId, Long posterId, String title, String description,
                          BigDecimal price, Task.PricingType pricingType, String location,
                          Double latitude, Double longitude, LocalDateTime deadline,
                          Task.TaskCategory category) {
        
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            throw new IllegalArgumentException("Task not found");
        }
        
        Task task = taskOpt.get();
        
        // Validate ownership
        if (!task.getPoster().getId().equals(posterId)) {
            throw new IllegalArgumentException("Only task poster can update the task");
        }
        
        // Only allow updates if task is still OPEN
        if (task.getStatus() != Task.TaskStatus.OPEN) {
            throw new IllegalArgumentException("Cannot update task that is not OPEN");
        }
        
        task.setTitle(title);
        task.setDescription(description);
        task.setPrice(price);
        task.setPricingType(pricingType);
        task.setLocation(location);
        task.setLatitude(latitude);
        task.setLongitude(longitude);
        task.setDeadline(deadline);
        task.setCategory(category);
        task.setUpdatedAt(LocalDateTime.now());
        
        return taskRepository.save(task);
    }
    
    /**
     * Cancel a task (only allowed if no one is assigned)
     */
    public boolean cancelTask(Long taskId, Long posterId, String reason) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        if (taskOpt.isEmpty()) {
            return false;
        }
        
        Task task = taskOpt.get();
        
        // Validate ownership
        if (!task.getPoster().getId().equals(posterId)) {
            throw new IllegalArgumentException("Only task poster can cancel the task");
        }
        
        // Only allow cancellation if no one is assigned
        if (task.getStatus() == Task.TaskStatus.IN_PROGRESS || 
            task.getStatus() == Task.TaskStatus.COMPLETED ||
            task.getStatus() == Task.TaskStatus.CONFIRMED) {
            throw new IllegalArgumentException("Cannot cancel task that is already in progress or completed");
        }
        
        task.setStatus(Task.TaskStatus.CANCELLED);
        task.setUpdatedAt(LocalDateTime.now());
        
        taskRepository.save(task);
        
        // Notify all applicants about cancellation
        List<TaskApplication> applications = taskApplicationRepository.findByTaskOrderByCreatedAtAsc(task);
        for (TaskApplication application : applications) {
            notificationService.sendApplicationRejectedNotification(application.getApplicant(), task);
        }
        
        return true;
    }
    
    // Task Application Management
    
    /**
     * Apply for a task
     */
    public TaskApplication applyForTask(Long taskId, Long applicantId, String message, Double proposedPrice, LocalDateTime estimatedCompletionTime) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<User> applicantOpt = userRepository.findById(applicantId);
        
        if (taskOpt.isEmpty() || applicantOpt.isEmpty()) {
            throw new IllegalArgumentException("Task or applicant not found");
        }
        
        Task task = taskOpt.get();
        User applicant = applicantOpt.get();
        
        // Validate task is open for applications
        if (task.getStatus() != Task.TaskStatus.OPEN) {
            throw new IllegalArgumentException("Task is not open for applications");
        }
        
        // Validate applicant is not the poster
        if (task.getPoster().getId().equals(applicantId)) {
            throw new IllegalArgumentException("Task poster cannot apply for their own task");
        }
        
        // Validate applicant is verified
        if (!applicant.isVerified()) {
            throw new IllegalArgumentException("User must be verified to apply for tasks");
        }
        
        // Check if user has already applied using the correct method
        Optional<TaskApplication> existingApplication = taskApplicationRepository.findApplicationByTaskAndApplicant(task, applicant);
        if (existingApplication.isPresent()) {
            throw new IllegalArgumentException("User has already applied for this task");
        }
        
        TaskApplication application = new TaskApplication();
        application.setTask(task);
        application.setApplicant(applicant);
        application.setMessage(message);
        application.setProposedPrice(proposedPrice);
        application.setEstimatedCompletionTime(estimatedCompletionTime);
        application.setStatus(TaskApplication.ApplicationStatus.PENDING);
        application.setCreatedAt(LocalDateTime.now());
        
        TaskApplication savedApplication = taskApplicationRepository.save(application);
        
        // Notify task poster about new application
        notificationService.sendTaskApplicationNotification(task.getPoster(), task, applicant);
        
        return savedApplication;
    }
    
    /**
     * Accept a task application
     */
    public boolean acceptApplication(Long taskId, Long applicationId, Long posterId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<TaskApplication> applicationOpt = taskApplicationRepository.findById(applicationId);
        
        if (taskOpt.isEmpty() || applicationOpt.isEmpty()) {
            return false;
        }
        
        Task task = taskOpt.get();
        TaskApplication application = applicationOpt.get();
        
        // Validate ownership
        if (!task.getPoster().getId().equals(posterId)) {
            throw new IllegalArgumentException("Only task poster can accept applications");
        }
        
        // Validate task is still open
        if (task.getStatus() != Task.TaskStatus.OPEN) {
            throw new IllegalArgumentException("Task is no longer open for applications");
        }
        
        // Accept the application
        application.setStatus(TaskApplication.ApplicationStatus.ACCEPTED);
        application.setRespondedAt(LocalDateTime.now());
        taskApplicationRepository.save(application);
        
        // Update task status and assign fulfiller
        task.setStatus(Task.TaskStatus.IN_PROGRESS);
        task.setFulfiller(application.getApplicant());
        task.setStartedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        // Update final price if different from original
        if (application.getProposedPrice() != null && 
            !application.getProposedPrice().equals(task.getPrice().doubleValue())) {
            task.setPrice(BigDecimal.valueOf(application.getProposedPrice()));
        }
        
        taskRepository.save(task);
        
        // Reject all other pending applications
        List<TaskApplication> otherApplications = taskApplicationRepository.findByTaskAndStatus(
                task, TaskApplication.ApplicationStatus.PENDING);
        
        for (TaskApplication otherApp : otherApplications) {
            if (!otherApp.getId().equals(applicationId)) {
                otherApp.setStatus(TaskApplication.ApplicationStatus.REJECTED);
                otherApp.setRespondedAt(LocalDateTime.now());
                taskApplicationRepository.save(otherApp);
                
                // Notify rejected applicants
                notificationService.sendApplicationRejectedNotification(otherApp.getApplicant(), task);
            }
        }
        
        // Notify accepted applicant
        notificationService.sendApplicationAcceptedNotification(application.getApplicant(), task);
        
        return true;
    }
    
    /**
     * Reject a task application
     */
    public boolean rejectApplication(Long applicationId, Long posterId) {
        Optional<TaskApplication> applicationOpt = taskApplicationRepository.findById(applicationId);
        
        if (applicationOpt.isEmpty()) {
            return false;
        }
        
        TaskApplication application = applicationOpt.get();
        Task task = application.getTask();
        
        // Validate ownership
        if (!task.getPoster().getId().equals(posterId)) {
            throw new IllegalArgumentException("Only task poster can reject applications");
        }
        
        // Only reject pending applications
        if (application.getStatus() != TaskApplication.ApplicationStatus.PENDING) {
            throw new IllegalArgumentException("Can only reject pending applications");
        }
        
        application.setStatus(TaskApplication.ApplicationStatus.REJECTED);
        application.setRespondedAt(LocalDateTime.now());
        
        taskApplicationRepository.save(application);
        
        // Notify rejected applicant
        notificationService.sendApplicationRejectedNotification(application.getApplicant(), task);
        
        return true;
    }
    
    // Task Completion Workflow
    
    /**
     * Mark task as completed by fulfiller
     */
    public boolean markTaskCompleted(Long taskId, Long fulfillerId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        
        if (taskOpt.isEmpty()) {
            return false;
        }
        
        Task task = taskOpt.get();
        
        // Validate fulfiller
        if (task.getFulfiller() == null || !task.getFulfiller().getId().equals(fulfillerId)) {
            throw new IllegalArgumentException("Only assigned fulfiller can mark task as completed");
        }
        
        // Validate task is in progress
        if (task.getStatus() != Task.TaskStatus.IN_PROGRESS) {
            throw new IllegalArgumentException("Task must be in progress to mark as completed");
        }
        
        task.setStatus(Task.TaskStatus.COMPLETED);
        task.setCompletedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        taskRepository.save(task);
        
        // Notify poster about completion
        notificationService.sendTaskCompletedNotification(task.getPoster(), task);
        
        return true;
    }
    
    /**
     * Confirm task completion by poster
     */
    public boolean confirmTaskCompletion(Long taskId, Long posterId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        
        if (taskOpt.isEmpty()) {
            return false;
        }
        
        Task task = taskOpt.get();
        
        // Validate poster
        if (!task.getPoster().getId().equals(posterId)) {
            throw new IllegalArgumentException("Only task poster can confirm completion");
        }
        
        // Validate task is completed
        if (task.getStatus() != Task.TaskStatus.COMPLETED) {
            throw new IllegalArgumentException("Task must be completed before confirmation");
        }
        
        task.setStatus(Task.TaskStatus.CONFIRMED);
        task.setConfirmedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());
        
        taskRepository.save(task);
        
        // Send review request notifications
        notificationService.sendReviewRequestNotification(task.getPoster(), task);
        notificationService.sendReviewRequestNotification(task.getFulfiller(), task);
        
        return true;
    }
    
    // Search and Discovery
    
    /**
     * Search tasks with basic filters
     */
    @Transactional(readOnly = true)
    public List<Task> searchTasks(String searchTerm, Task.TaskCategory category, 
                                 Task.TaskStatus status, int limit, int offset) {
        
        // Priority order: status first, then category, then default to OPEN
        if (status != null) {
            return taskRepository.findAllByStatusEager(status)
                    .stream()
                    .skip(offset)
                    .limit(limit)
                    .toList();
        } else if (category != null) {
            return taskRepository.findAllByCategoryEager(category)
                    .stream()
                    .filter(task -> task.getStatus() == Task.TaskStatus.OPEN) // Only OPEN tasks by default
                    .skip(offset)
                    .limit(limit)
                    .toList();
        } else {
            return taskRepository.findAllByStatusEager(Task.TaskStatus.OPEN)
                    .stream()
                    .skip(offset)
                    .limit(limit)
                    .toList();
        }
    }
    
    /**
     * Get tasks by poster
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByPoster(Long posterId) {
        Optional<User> posterOpt = userRepository.findById(posterId);
        return posterOpt.map(poster -> taskRepository.findByPoster(poster))
                      .orElse(List.of());
    }
    
    /**
     * Get tasks by fulfiller
     */
    @Transactional(readOnly = true)
    public List<Task> getTasksByFulfiller(Long fulfillerId) {
        Optional<User> fulfillerOpt = userRepository.findById(fulfillerId);
        return fulfillerOpt.map(fulfiller -> taskRepository.findByFulfiller(fulfiller))
                          .orElse(List.of());
    }
    
    // Task Applications for User
    
    /**
     * Get applications by user
     */
    @Transactional(readOnly = true)
    public List<TaskApplication> getApplicationsByUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isPresent()) {
            return taskApplicationRepository.findByApplicantOrderByCreatedAtDesc(userOpt.get());
        }
        return List.of();
    }
    
    /**
     * Get applications for a task
     */
    @Transactional(readOnly = true)
    public List<TaskApplication> getApplicationsForTask(Long taskId, Long posterId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        
        if (taskOpt.isEmpty()) {
            return List.of();
        }
        
        Task task = taskOpt.get();
        
        // Validate ownership
        if (!task.getPoster().getId().equals(posterId)) {
            throw new IllegalArgumentException("Only task poster can view applications");
        }
        
        return taskApplicationRepository.findByTaskOrderByCreatedAtAsc(task);
    }
    
    // Utility Methods
    
    /**
     * Get task by ID
     */
    @Transactional(readOnly = true)
    public Optional<Task> getTaskById(Long taskId) {
        Task task = taskRepository.findByIdWithUsersEager(taskId);
        return Optional.ofNullable(task);
    }
    
    /**
     * Check if user can edit task
     */
    @Transactional(readOnly = true)
    public boolean canUserEditTask(Long taskId, Long userId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        return taskOpt.map(task -> task.getPoster().getId().equals(userId) && 
                                  task.getStatus() == Task.TaskStatus.OPEN)
                     .orElse(false);
    }
    
    /**
     * Check if user can apply for task
     */
    @Transactional(readOnly = true)
    public boolean canUserApplyForTask(Long taskId, Long userId) {
        Optional<Task> taskOpt = taskRepository.findById(taskId);
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (taskOpt.isEmpty() || userOpt.isEmpty()) {
            return false;
        }
        
        Task task = taskOpt.get();
        User user = userOpt.get();
        
        // Check basic conditions
        if (task.getStatus() != Task.TaskStatus.OPEN || 
            task.getPoster().getId().equals(userId) ||
            !user.isVerified()) {
            return false;
        }
        
        // Check if user has already applied
        Optional<TaskApplication> existingApp = taskApplicationRepository.findApplicationByTaskAndApplicant(task, user);
        return existingApp.isEmpty();
    }
    
    // Basic Analytics Methods
    
    /**
     * Get task count by status for user
     */
    @Transactional(readOnly = true)
    public Long getTaskCountByStatus(Long userId, Task.TaskStatus status, boolean asPoster) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return 0L;
        }
        
        User user = userOpt.get();
        List<Task> allTasks;
        
        if (asPoster) {
            allTasks = taskRepository.findByPoster(user);
        } else {
            allTasks = taskRepository.findByFulfiller(user);
        }
        
        // Filter by status
        return allTasks.stream()
                      .filter(task -> task.getStatus() == status)
                      .count();
    }
    
    /**
     * Get total task count for user
     */
    @Transactional(readOnly = true)
    public Long getTotalTaskCount(Long userId, boolean asPoster) {
        Optional<User> userOpt = userRepository.findById(userId);
        if (userOpt.isEmpty()) {
            return 0L;
        }
        
        User user = userOpt.get();
        List<Task> tasks;
        
        if (asPoster) {
            tasks = taskRepository.findByPoster(user);
        } else {
            tasks = taskRepository.findByFulfiller(user);
        }
        
        return (long) tasks.size();
    }
}
