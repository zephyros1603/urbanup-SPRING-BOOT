package com.zephyros.urbanup.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zephyros.urbanup.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    // Basic finder methods
    Optional<User> findByEmail(String email);
    
    Optional<User> findByPhoneNumber(String phoneNumber);
    
    boolean existsByEmail(String email);
    
    boolean existsByPhoneNumber(String phoneNumber);
    
    // Active users
    List<User> findByIsActiveTrue();
    
    List<User> findByIsActiveFalse();
    
    // Verification status
    List<User> findByIsEmailVerified(Boolean isEmailVerified);
    
    List<User> findByIsPhoneVerified(Boolean isPhoneVerified);
    
    List<User> findByIsEmailVerifiedAndIsPhoneVerified(Boolean isEmailVerified, Boolean isPhoneVerified);
    
    // Rating-based queries
    @Query("SELECT u FROM User u WHERE u.ratingAsPoster >= :minRating ORDER BY u.ratingAsPoster DESC")
    List<User> findTopRatedPosters(@Param("minRating") Double minRating);
    
    @Query("SELECT u FROM User u WHERE u.ratingAsFulfiller >= :minRating ORDER BY u.ratingAsFulfiller DESC")
    List<User> findTopRatedFulfillers(@Param("minRating") Double minRating);
    
    @Query("SELECT u FROM User u WHERE " +
           "((u.ratingAsPoster * u.ratingsAsPostCount) + (u.ratingAsFulfiller * u.ratingsAsFulfillerCount)) / " +
           "(u.ratingsAsPostCount + u.ratingsAsFulfillerCount) >= :minRating " +
           "ORDER BY ((u.ratingAsPoster * u.ratingsAsPostCount) + (u.ratingAsFulfiller * u.ratingsAsFulfillerCount)) / " +
           "(u.ratingsAsPostCount + u.ratingsAsFulfillerCount) DESC")
    List<User> findTopRatedUsers(@Param("minRating") Double minRating);
    
    // Recent activity
    @Query("SELECT u FROM User u WHERE u.lastLogin >= :since ORDER BY u.lastLogin DESC")
    List<User> findRecentlyActiveUsers(@Param("since") LocalDateTime since);
    
    @Query("SELECT u FROM User u WHERE u.createdAt >= :since ORDER BY u.createdAt DESC")
    List<User> findRecentlyRegisteredUsers(@Param("since") LocalDateTime since);
    
    // Theme preferences
    List<User> findByTheme(User.UserTheme theme);
    
    // Search functionality
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(u.firstName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.lastName) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> searchUsers(@Param("searchTerm") String searchTerm);
    
    // Account creation source
    List<User> findByAccountCreatedFrom(String source);
    
    // Custom queries for analytics
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate AND u.createdAt <= :endDate")
    Long countUsersRegisteredBetween(@Param("startDate") LocalDateTime startDate, 
                                    @Param("endDate") LocalDateTime endDate);
    
    @Query("SELECT COUNT(u) FROM User u WHERE u.isEmailVerified = true AND u.isPhoneVerified = true")
    Long countVerifiedUsers();
    
    @Query("SELECT AVG((u.ratingAsPoster * u.ratingsAsPostCount + u.ratingAsFulfiller * u.ratingsAsFulfillerCount) / " +
           "(u.ratingsAsPostCount + u.ratingsAsFulfillerCount)) FROM User u WHERE " +
           "u.ratingsAsPostCount > 0 OR u.ratingsAsFulfillerCount > 0")
    Double getAverageUserRating();
}
