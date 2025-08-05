package com.zephyros.urbanup.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.zephyros.urbanup.model.Task;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.model.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
    
    // Find profile by user
    Optional<UserProfile> findByUser(User user);
    
    Optional<UserProfile> findByUserId(Long userId);
    
    // Verification status queries
    List<UserProfile> findByKycStatus(UserProfile.KYCStatus kycStatus);
    
    List<UserProfile> findByGovernmentIdVerifiedTrue();
    
    List<UserProfile> findByPhoneVerifiedTrue();
    
    List<UserProfile> findByEmailVerifiedTrue();
    
    List<UserProfile> findByAddressVerifiedTrue();
    
    // Fully verified users
    @Query("SELECT up FROM UserProfile up WHERE up.phoneVerified = true AND up.emailVerified = true AND up.kycStatus = 'VERIFIED'")
    List<UserProfile> findVerifiedUserProfiles();
    
    @Query("SELECT up FROM UserProfile up WHERE up.phoneVerified = true AND up.emailVerified = true AND up.addressVerified = true AND up.governmentIdVerified = true AND up.kycStatus = 'VERIFIED'")
    List<UserProfile> findFullyVerifiedUserProfiles();
    
    // Background check queries
    List<UserProfile> findByBackgroundCheckStatus(UserProfile.BackgroundCheckStatus status);
    
    @Query("SELECT up FROM UserProfile up WHERE up.backgroundCheckStatus = 'PASSED'")
    List<UserProfile> findProfilesWithPassedBackgroundCheck();
    
    // Skills and interests
    @Query("SELECT up FROM UserProfile up WHERE :skill MEMBER OF up.skills")
    List<UserProfile> findProfilesBySkill(@Param("skill") Task.TaskCategory skill);
    
    @Query("SELECT up FROM UserProfile up WHERE :interest MEMBER OF up.interests")
    List<UserProfile> findProfilesByInterest(@Param("interest") Task.TaskCategory interest);
    
    // Location-based queries
    @Query("SELECT up FROM UserProfile up WHERE up.city = :city")
    List<UserProfile> findProfilesByCity(@Param("city") String city);
    
    @Query("SELECT up FROM UserProfile up WHERE up.state = :state")
    List<UserProfile> findProfilesByState(@Param("state") String state);
    
    @Query("SELECT up FROM UserProfile up WHERE up.country = :country")
    List<UserProfile> findProfilesByCountry(@Param("country") String country);
    
    // Location radius queries
    @Query("SELECT up FROM UserProfile up WHERE up.homeLatitude IS NOT NULL AND up.homeLongitude IS NOT NULL AND " +
           "6371 * acos(cos(radians(:latitude)) * cos(radians(up.homeLatitude)) * " +
           "cos(radians(up.homeLongitude) - radians(:longitude)) + " +
           "sin(radians(:latitude)) * sin(radians(up.homeLatitude))) <= :radiusKm")
    List<UserProfile> findProfilesWithinRadius(@Param("latitude") Double latitude, 
                                              @Param("longitude") Double longitude, 
                                              @Param("radiusKm") Double radiusKm);
    
    // Demographics
    List<UserProfile> findByGender(UserProfile.Gender gender);
    
    @Query("SELECT up FROM UserProfile up WHERE up.dateOfBirth BETWEEN :startDate AND :endDate")
    List<UserProfile> findProfilesByAgeRange(@Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);
    
    // Availability status
    List<UserProfile> findByAvailabilityStatus(UserProfile.AvailabilityStatus status);
    
    @Query("SELECT up FROM UserProfile up WHERE up.availabilityStatus = 'AVAILABLE'")
    List<UserProfile> findAvailableUserProfiles();
    
    // Activity-based queries
    @Query("SELECT up FROM UserProfile up WHERE up.tasksPosted >= :minTasks ORDER BY up.tasksPosted DESC")
    List<UserProfile> findActivePosters(@Param("minTasks") Integer minTasks);
    
    @Query("SELECT up FROM UserProfile up WHERE up.tasksCompleted >= :minTasks ORDER BY up.tasksCompleted DESC")
    List<UserProfile> findActiveFulfillers(@Param("minTasks") Integer minTasks);
    
    @Query("SELECT up FROM UserProfile up WHERE up.totalEarnings >= :minEarnings ORDER BY up.totalEarnings DESC")
    List<UserProfile> findTopEarners(@Param("minEarnings") Double minEarnings);
    
    @Query("SELECT up FROM UserProfile up WHERE up.totalSpent >= :minSpending ORDER BY up.totalSpent DESC")
    List<UserProfile> findTopSpenders(@Param("minSpending") Double minSpending);
    
    // Badge-based queries
    @Query("SELECT up FROM UserProfile up WHERE :badge MEMBER OF up.badges")
    List<UserProfile> findProfilesByBadge(@Param("badge") UserProfile.UserBadge badge);
    
    @Query("SELECT up FROM UserProfile up WHERE SIZE(up.badges) >= :minBadges ORDER BY SIZE(up.badges) DESC")
    List<UserProfile> findProfilesWithMultipleBadges(@Param("minBadges") Integer minBadges);
    
    // Location update tracking
    @Query("SELECT up FROM UserProfile up WHERE up.lastLocationUpdate >= :since ORDER BY up.lastLocationUpdate DESC")
    List<UserProfile> findProfilesWithRecentLocationUpdate(@Param("since") LocalDateTime since);
    
    @Query("SELECT up FROM UserProfile up WHERE up.lastLocationUpdate IS NULL OR up.lastLocationUpdate < :threshold")
    List<UserProfile> findProfilesWithStaleLocation(@Param("threshold") LocalDateTime threshold);
    
    // Search functionality
    @Query("SELECT up FROM UserProfile up WHERE " +
           "LOWER(up.bio) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(up.city) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(up.state) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<UserProfile> searchUserProfiles(@Param("searchTerm") String searchTerm);
    
    // Analytics
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.kycStatus = :status")
    Long countProfilesByKycStatus(@Param("status") UserProfile.KYCStatus status);
    
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.gender = :gender")
    Long countProfilesByGender(@Param("gender") UserProfile.Gender gender);
    
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.country = :country")
    Long countProfilesByCountry(@Param("country") String country);
    
    @Query("SELECT AVG(up.tasksPosted) FROM UserProfile up WHERE up.tasksPosted > 0")
    Double getAverageTasksPosted();
    
    @Query("SELECT AVG(up.tasksCompleted) FROM UserProfile up WHERE up.tasksCompleted > 0")
    Double getAverageTasksCompleted();
    
    @Query("SELECT AVG(up.totalEarnings) FROM UserProfile up WHERE up.totalEarnings > 0")
    Double getAverageTotalEarnings();
    
    @Query("SELECT AVG(up.totalSpent) FROM UserProfile up WHERE up.totalSpent > 0")
    Double getAverageTotalSpent();
    
    // Distribution queries
    @Query("SELECT up.gender, COUNT(up) FROM UserProfile up WHERE up.gender IS NOT NULL GROUP BY up.gender")
    List<Object[]> getGenderDistribution();
    
    @Query("SELECT up.country, COUNT(up) FROM UserProfile up WHERE up.country IS NOT NULL GROUP BY up.country")
    List<Object[]> getCountryDistribution();
    
    @Query("SELECT up.kycStatus, COUNT(up) FROM UserProfile up GROUP BY up.kycStatus")
    List<Object[]> getKycStatusDistribution();
    
    @Query("SELECT up.availabilityStatus, COUNT(up) FROM UserProfile up GROUP BY up.availabilityStatus")
    List<Object[]> getAvailabilityStatusDistribution();
    
    // Check if profile exists
    boolean existsByUser(User user);
    
    boolean existsByUserId(Long userId);
}
