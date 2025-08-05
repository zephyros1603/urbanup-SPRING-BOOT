package com.zephyros.urbanup.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.TaskApplication;
import com.zephyros.urbanup.model.User;

@Repository
public interface TaskApplicationRepository extends JpaRepository<TaskApplication, Long> {
    
    // Find applications by task
    List<TaskApplication> findByTaskOrderByCreatedAtAsc(Task task);
    
    List<TaskApplication> findByTaskIdOrderByCreatedAtAsc(Long taskId);
    
    // Find applications by applicant
    List<TaskApplication> findByApplicantOrderByCreatedAtDesc(User applicant);
    
    // Find applications by status
    List<TaskApplication> findByStatus(TaskApplication.ApplicationStatus status);
    
    Page<TaskApplication> findByStatusOrderByCreatedAtDesc(TaskApplication.ApplicationStatus status, Pageable pageable);
    
    // Find applications by task and status
    List<TaskApplication> findByTaskAndStatus(Task task, TaskApplication.ApplicationStatus status);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task.id = :taskId AND ta.status = :status ORDER BY ta.createdAt ASC")
    List<TaskApplication> findByTaskIdAndStatus(@Param("taskId") Long taskId, 
                                               @Param("status") TaskApplication.ApplicationStatus status);
    
    // Find applications by applicant and status
    List<TaskApplication> findByApplicantAndStatus(User applicant, TaskApplication.ApplicationStatus status);
    
    // Pending applications
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.status = 'PENDING' ORDER BY ta.createdAt ASC")
    List<TaskApplication> findPendingApplications();
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND ta.status = 'PENDING' ORDER BY ta.createdAt ASC")
    List<TaskApplication> findPendingApplicationsForTask(@Param("task") Task task);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.applicant = :applicant AND ta.status = 'PENDING' ORDER BY ta.createdAt DESC")
    List<TaskApplication> findPendingApplicationsByApplicant(@Param("applicant") User applicant);
    
    // Accepted applications
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.status = 'ACCEPTED' ORDER BY ta.respondedAt DESC")
    List<TaskApplication> findAcceptedApplications();
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND ta.status = 'ACCEPTED'")
    Optional<TaskApplication> findAcceptedApplicationForTask(@Param("task") Task task);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.applicant = :applicant AND ta.status = 'ACCEPTED' ORDER BY ta.respondedAt DESC")
    List<TaskApplication> findAcceptedApplicationsByApplicant(@Param("applicant") User applicant);
    
    // Rejected applications
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.status = 'REJECTED' ORDER BY ta.respondedAt DESC")
    List<TaskApplication> findRejectedApplications();
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND ta.status = 'REJECTED' ORDER BY ta.respondedAt DESC")
    List<TaskApplication> findRejectedApplicationsForTask(@Param("task") Task task);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.applicant = :applicant AND ta.status = 'REJECTED' ORDER BY ta.respondedAt DESC")
    List<TaskApplication> findRejectedApplicationsByApplicant(@Param("applicant") User applicant);
    
    // Withdrawn applications
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.status = 'WITHDRAWN' ORDER BY ta.respondedAt DESC")
    List<TaskApplication> findWithdrawnApplications();
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.applicant = :applicant AND ta.status = 'WITHDRAWN' ORDER BY ta.respondedAt DESC")
    List<TaskApplication> findWithdrawnApplicationsByApplicant(@Param("applicant") User applicant);
    
    // Find applications for tasks posted by a specific user
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task.poster = :poster ORDER BY ta.createdAt DESC")
    List<TaskApplication> findApplicationsForTasksPostedBy(@Param("poster") User poster);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task.poster = :poster AND ta.status = :status ORDER BY ta.createdAt DESC")
    List<TaskApplication> findApplicationsForTasksPostedByWithStatus(@Param("poster") User poster, 
                                                                    @Param("status") TaskApplication.ApplicationStatus status);
    
    // Check if user has already applied for a task
    @Query("SELECT CASE WHEN COUNT(ta) > 0 THEN true ELSE false END FROM TaskApplication ta WHERE ta.task = :task AND ta.applicant = :applicant")
    boolean hasUserAppliedForTask(@Param("task") Task task, @Param("applicant") User applicant);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND ta.applicant = :applicant")
    Optional<TaskApplication> findApplicationByTaskAndApplicant(@Param("task") Task task, @Param("applicant") User applicant);
    
    // Time-based queries
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.createdAt >= :since ORDER BY ta.createdAt DESC")
    List<TaskApplication> findRecentApplications(@Param("since") LocalDateTime since);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.respondedAt >= :since ORDER BY ta.respondedAt DESC")
    List<TaskApplication> findRecentlyRespondedApplications(@Param("since") LocalDateTime since);
    
    // Applications with proposed price
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.proposedPrice IS NOT NULL ORDER BY ta.proposedPrice ASC")
    List<TaskApplication> findApplicationsWithProposedPrice();
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND ta.proposedPrice IS NOT NULL ORDER BY ta.proposedPrice ASC")
    List<TaskApplication> findApplicationsWithProposedPriceForTask(@Param("task") Task task);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND ta.proposedPrice <= :maxPrice ORDER BY ta.proposedPrice ASC")
    List<TaskApplication> findApplicationsWithPriceUnder(@Param("task") Task task, @Param("maxPrice") Double maxPrice);
    
    // Applications by estimated completion time
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.estimatedCompletionTime IS NOT NULL ORDER BY ta.estimatedCompletionTime ASC")
    List<TaskApplication> findApplicationsWithEstimatedTime();
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND ta.estimatedCompletionTime IS NOT NULL ORDER BY ta.estimatedCompletionTime ASC")
    List<TaskApplication> findApplicationsWithEstimatedTimeForTask(@Param("task") Task task);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND ta.estimatedCompletionTime <= :maxHours ORDER BY ta.estimatedCompletionTime ASC")
    List<TaskApplication> findApplicationsWithTimeUnder(@Param("task") Task task, @Param("maxHours") Integer maxHours);
    
    // Search applications by message content
    @Query("SELECT ta FROM TaskApplication ta WHERE LOWER(ta.message) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY ta.createdAt DESC")
    List<TaskApplication> searchApplicationsByMessage(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.task = :task AND LOWER(ta.message) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY ta.createdAt ASC")
    List<TaskApplication> searchApplicationsForTaskByMessage(@Param("task") Task task, @Param("searchTerm") String searchTerm);
    
    // Analytics and statistics
    @Query("SELECT COUNT(ta) FROM TaskApplication ta WHERE ta.task = :task")
    Long countApplicationsForTask(@Param("task") Task task);
    
    @Query("SELECT COUNT(ta) FROM TaskApplication ta WHERE ta.task = :task AND ta.status = :status")
    Long countApplicationsForTaskByStatus(@Param("task") Task task, @Param("status") TaskApplication.ApplicationStatus status);
    
    @Query("SELECT COUNT(ta) FROM TaskApplication ta WHERE ta.applicant = :applicant")
    Long countApplicationsByApplicant(@Param("applicant") User applicant);
    
    @Query("SELECT COUNT(ta) FROM TaskApplication ta WHERE ta.applicant = :applicant AND ta.status = :status")
    Long countApplicationsByApplicantAndStatus(@Param("applicant") User applicant, 
                                              @Param("status") TaskApplication.ApplicationStatus status);
    
    @Query("SELECT COUNT(ta) FROM TaskApplication ta WHERE ta.status = :status")
    Long countApplicationsByStatus(@Param("status") TaskApplication.ApplicationStatus status);
    
    @Query("SELECT COUNT(ta) FROM TaskApplication ta WHERE ta.createdAt >= :startDate AND ta.createdAt <= :endDate")
    Long countApplicationsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                        @Param("endDate") LocalDateTime endDate);
    
    // Application success rate for users
    @Query("SELECT COUNT(ta) FROM TaskApplication ta WHERE ta.applicant = :applicant AND ta.status = 'ACCEPTED'")
    Long countAcceptedApplicationsByApplicant(@Param("applicant") User applicant);
    
    @Query("SELECT ta.status, COUNT(ta) FROM TaskApplication ta WHERE ta.applicant = :applicant GROUP BY ta.status")
    List<Object[]> getApplicationStatusDistributionForApplicant(@Param("applicant") User applicant);
    
    // Task poster's response rate
    @Query("SELECT COUNT(ta) FROM TaskApplication ta WHERE ta.task.poster = :poster AND ta.status != 'PENDING'")
    Long countRespondedApplicationsForPoster(@Param("poster") User poster);
    
    @Query("SELECT ta.status, COUNT(ta) FROM TaskApplication ta WHERE ta.task.poster = :poster GROUP BY ta.status")
    List<Object[]> getApplicationResponseDistributionForPoster(@Param("poster") User poster);
    
    // Average response time
    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, ta.createdAt, ta.respondedAt)) FROM TaskApplication ta WHERE ta.respondedAt IS NOT NULL")
    Double getAverageResponseTimeInHours();
    
    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, ta.createdAt, ta.respondedAt)) FROM TaskApplication ta WHERE ta.task.poster = :poster AND ta.respondedAt IS NOT NULL")
    Double getAverageResponseTimeForPoster(@Param("poster") User poster);
    
    // Distribution queries
    @Query("SELECT ta.status, COUNT(ta) FROM TaskApplication ta GROUP BY ta.status")
    List<Object[]> getApplicationStatusDistribution();
    
    // Old unanswered applications
    @Query("SELECT ta FROM TaskApplication ta WHERE ta.status = 'PENDING' AND ta.createdAt < :threshold ORDER BY ta.createdAt ASC")
    List<TaskApplication> findOldUnansweredApplications(@Param("threshold") LocalDateTime threshold);
}
