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

import com.zephyros.urbanup.model.Review;
import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Find review by task
    Optional<Review> findByTask(Task task);
    
    Optional<Review> findByTaskId(Long taskId);
    
    // Find reviews by reviewer
    List<Review> findByReviewerOrderByCreatedAtDesc(User reviewer);
    
    // Find reviews by reviewee
    List<Review> findByRevieweeOrderByCreatedAtDesc(User reviewee);
    
    // Find reviews by type
    List<Review> findByReviewType(Review.ReviewType reviewType);
    
    @Query("SELECT r FROM Review r WHERE r.reviewee = :user AND r.reviewType = :reviewType ORDER BY r.createdAt DESC")
    List<Review> findReviewsByUserAndType(@Param("user") User user, @Param("reviewType") Review.ReviewType reviewType);
    
    // Reviews for a user as poster
    @Query("SELECT r FROM Review r WHERE r.reviewee = :user AND r.reviewType = 'FULFILLER_TO_POSTER' ORDER BY r.createdAt DESC")
    List<Review> findReviewsForUserAsPoster(@Param("user") User user);
    
    // Reviews for a user as fulfiller
    @Query("SELECT r FROM Review r WHERE r.reviewee = :user AND r.reviewType = 'POSTER_TO_FULFILLER' ORDER BY r.createdAt DESC")
    List<Review> findReviewsForUserAsFulfiller(@Param("user") User user);
    
    // Rating-based queries
    List<Review> findByRating(Integer rating);
    
    List<Review> findByRatingGreaterThanEqual(Integer minRating);
    
    List<Review> findByRatingLessThanEqual(Integer maxRating);
    
    @Query("SELECT r FROM Review r WHERE r.reviewee = :user AND r.rating >= :minRating ORDER BY r.rating DESC")
    List<Review> findHighRatingReviewsForUser(@Param("user") User user, @Param("minRating") Integer minRating);
    
    @Query("SELECT r FROM Review r WHERE r.reviewee = :user AND r.rating <= :maxRating ORDER BY r.rating ASC")
    List<Review> findLowRatingReviewsForUser(@Param("user") User user, @Param("maxRating") Integer maxRating);
    
    // Anonymous reviews
    List<Review> findByIsAnonymousTrue();
    
    @Query("SELECT r FROM Review r WHERE r.reviewee = :user AND r.isAnonymous = false ORDER BY r.createdAt DESC")
    List<Review> findPublicReviewsForUser(@Param("user") User user);
    
    // Recent reviews
    @Query("SELECT r FROM Review r WHERE r.createdAt >= :since ORDER BY r.createdAt DESC")
    List<Review> findRecentReviews(@Param("since") LocalDateTime since);
    
    @Query("SELECT r FROM Review r WHERE r.reviewee = :user AND r.createdAt >= :since ORDER BY r.createdAt DESC")
    List<Review> findRecentReviewsForUser(@Param("user") User user, @Param("since") LocalDateTime since);
    
    // Search reviews by comment
    @Query("SELECT r FROM Review r WHERE LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY r.createdAt DESC")
    List<Review> searchReviewsByComment(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT r FROM Review r WHERE r.reviewee = :user AND LOWER(r.comment) LIKE LOWER(CONCAT('%', :searchTerm, '%')) ORDER BY r.createdAt DESC")
    List<Review> searchReviewsForUser(@Param("user") User user, @Param("searchTerm") String searchTerm);
    
    // Pagination support
    Page<Review> findByRevieweeOrderByCreatedAtDesc(User reviewee, Pageable pageable);
    
    Page<Review> findByReviewerOrderByCreatedAtDesc(User reviewer, Pageable pageable);
    
    // Statistics and analytics
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee = :user")
    Double getAverageRatingForUser(@Param("user") User user);
    
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.reviewee = :user AND r.reviewType = :reviewType")
    Double getAverageRatingForUserByType(@Param("user") User user, @Param("reviewType") Review.ReviewType reviewType);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee = :user")
    Long countReviewsForUser(@Param("user") User user);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee = :user AND r.reviewType = :reviewType")
    Long countReviewsForUserByType(@Param("user") User user, @Param("reviewType") Review.ReviewType reviewType);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.reviewee = :user AND r.rating = :rating")
    Long countReviewsForUserByRating(@Param("user") User user, @Param("rating") Integer rating);
    
    @Query("SELECT COUNT(r) FROM Review r WHERE r.createdAt >= :startDate AND r.createdAt <= :endDate")
    Long countReviewsCreatedBetween(@Param("startDate") LocalDateTime startDate, 
                                   @Param("endDate") LocalDateTime endDate);
    
    // Rating distribution
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.reviewee = :user GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getRatingDistributionForUser(@Param("user") User user);
    
    @Query("SELECT r.rating, COUNT(r) FROM Review r GROUP BY r.rating ORDER BY r.rating")
    List<Object[]> getOverallRatingDistribution();
    
    // Check if review exists
    boolean existsByTask(Task task);
    
    boolean existsByTaskId(Long taskId);
    
    @Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Review r WHERE r.task = :task AND r.reviewer = :reviewer")
    boolean existsByTaskAndReviewer(@Param("task") Task task, @Param("reviewer") User reviewer);
}
