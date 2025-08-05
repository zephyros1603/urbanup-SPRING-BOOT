package com.zephyros.urbanup.repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    
    // Eager fetching for all tasks with user relationships
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.poster LEFT JOIN FETCH t.fulfiller")
    List<Task> findAllWithUsersEager();
    
    // Eager fetching versions for common queries
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.poster LEFT JOIN FETCH t.fulfiller WHERE t.status = :status")
    List<Task> findAllByStatusEager(@Param("status") Task.TaskStatus status);
    
    @Query("SELECT t FROM Task t LEFT JOIN FETCH t.poster LEFT JOIN FETCH t.fulfiller WHERE t.category = :category")
    List<Task> findAllByCategoryEager(@Param("category") Task.TaskCategory category);
    
    // Basic status queries
    List<Task> findByStatus(Task.TaskStatus status);
    
    Page<Task> findByStatus(Task.TaskStatus status, Pageable pageable);
    
    // Available tasks (open status)
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' ORDER BY t.createdAt DESC")
    List<Task> findAvailableTasks();
    
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' ORDER BY t.createdAt DESC")
    Page<Task> findAvailableTasks(Pageable pageable);
    
    // User-specific queries
    List<Task> findByPoster(User poster);
    
    List<Task> findByFulfiller(User fulfiller);
    
    List<Task> findByPosterOrderByCreatedAtDesc(User poster);
    
    List<Task> findByFulfillerOrderByCreatedAtDesc(User fulfiller);
    
    // Category-based queries
    List<Task> findByCategory(Task.TaskCategory category);
    
    Page<Task> findByCategory(Task.TaskCategory category, Pageable pageable);
    
    @Query("SELECT t FROM Task t WHERE t.category = :category AND t.status = 'OPEN'")
    List<Task> findAvailableTasksByCategory(@Param("category") Task.TaskCategory category);
    
    // Location-based queries
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' AND " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(t.latitude)) * " +
           "cos(radians(t.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(t.latitude))) <= :radiusKm " +
           "ORDER BY 6371 * acos(cos(radians(:latitude)) * cos(radians(t.latitude)) * " +
           "cos(radians(t.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(t.latitude)))")
    List<Task> findNearbyAvailableTasks(@Param("latitude") Double latitude, 
                                       @Param("longitude") Double longitude, 
                                       @Param("radiusKm") Double radiusKm);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' AND t.category = :category AND " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(t.latitude)) * " +
           "cos(radians(t.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(t.latitude))) <= :radiusKm " +
           "ORDER BY 6371 * acos(cos(radians(:latitude)) * cos(radians(t.latitude)) * " +
           "cos(radians(t.longitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(t.latitude)))")
    List<Task> findNearbyAvailableTasksByCategory(@Param("latitude") Double latitude, 
                                                 @Param("longitude") Double longitude, 
                                                 @Param("radiusKm") Double radiusKm,
                                                 @Param("category") Task.TaskCategory category);
    
    // Price-based queries
    List<Task> findByPriceBetween(BigDecimal minPrice, BigDecimal maxPrice);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' AND t.price >= :minPrice AND t.price <= :maxPrice ORDER BY t.price ASC")
    List<Task> findAvailableTasksByPriceRange(@Param("minPrice") BigDecimal minPrice, 
                                             @Param("maxPrice") BigDecimal maxPrice);
    
    // Pricing type queries
    List<Task> findByPricingType(Task.PricingType pricingType);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' AND t.pricingType = :pricingType")
    List<Task> findAvailableTasksByPricingType(@Param("pricingType") Task.PricingType pricingType);
    
    // Urgent tasks
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' AND t.isUrgent = true ORDER BY t.createdAt DESC")
    List<Task> findUrgentAvailableTasks();
    
    // Deadline-based queries
    @Query("SELECT t FROM Task t WHERE t.status IN ('OPEN', 'ACCEPTED', 'IN_PROGRESS') AND t.deadline <= :deadline")
    List<Task> findTasksWithUpcomingDeadline(@Param("deadline") LocalDateTime deadline);
    
    @Query("SELECT t FROM Task t WHERE t.status IN ('ACCEPTED', 'IN_PROGRESS') AND t.deadline < :now")
    List<Task> findOverdueTasks(@Param("now") LocalDateTime now);
    
    // Search functionality
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' AND " +
           "(LOWER(t.title) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.location) LIKE LOWER(CONCAT('%', :searchTerm, '%')))")
    List<Task> searchAvailableTasks(@Param("searchTerm") String searchTerm);
    
    // Time-based queries
    @Query("SELECT t FROM Task t WHERE t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Task> findRecentTasks(@Param("since") LocalDateTime since);
    
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' AND t.createdAt >= :since ORDER BY t.createdAt DESC")
    List<Task> findRecentAvailableTasks(@Param("since") LocalDateTime since);
    
    // Analytics queries
    @Query("SELECT COUNT(t) FROM Task t WHERE t.status = :status")
    Long countTasksByStatus(@Param("status") Task.TaskStatus status);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.category = :category")
    Long countTasksByCategory(@Param("category") Task.TaskCategory category);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.createdAt >= :startDate AND t.createdAt <= :endDate")
    Long countTasksCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                 @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT AVG(t.price) FROM Task t WHERE t.category = :category")
    BigDecimal getAveragePriceByCategory(@Param("category") Task.TaskCategory category);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.poster = :user")
    Long countTasksPostedByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(t) FROM Task t WHERE t.fulfiller = :user AND t.status IN ('COMPLETED', 'CONFIRMED')")
    Long countTasksCompletedByUser(@Param("user") User user);
    
    // Complex filtering
    @Query("SELECT t FROM Task t WHERE t.status = 'OPEN' " +
           "AND (:category IS NULL OR t.category = :category) " +
           "AND (:minPrice IS NULL OR t.price >= :minPrice) " +
           "AND (:maxPrice IS NULL OR t.price <= :maxPrice) " +
           "AND (:pricingType IS NULL OR t.pricingType = :pricingType) " +
           "AND (:isUrgent IS NULL OR t.isUrgent = :isUrgent) " +
           "ORDER BY t.createdAt DESC")
    Page<Task> findAvailableTasksWithFilters(@Param("category") Task.TaskCategory category,
                                            @Param("minPrice") BigDecimal minPrice,
                                            @Param("maxPrice") BigDecimal maxPrice,
                                            @Param("pricingType") Task.PricingType pricingType,
                                            @Param("isUrgent") Boolean isUrgent,
                                            Pageable pageable);
}
