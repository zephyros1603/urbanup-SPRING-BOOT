package com.zephyros.urbanup.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.model.UserProfile;
import com.zephyros.urbanup.repository.UserProfileRepository;
import com.zephyros.urbanup.repository.UserRepository;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private UserProfileRepository userProfileRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private NotificationService notificationService;
    
    // User Registration and Authentication
    
    /**
     * Register a new user with basic information
     */
    public User registerUser(String firstName, String lastName, String email, 
                           String phoneNumber, String password, String accountCreatedFrom) {
        // Validate email and phone uniqueness
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists");
        }
        
        if (userRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalArgumentException("Phone number already exists");
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhoneNumber(phoneNumber);
        user.setPassword(passwordEncoder.encode(password));
        user.setAccountCreatedFrom(accountCreatedFrom);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setIsActive(true);
        user.setTheme(User.UserTheme.LIGHT);
        
        // Save user
        User savedUser = userRepository.save(user);
        
        // Create associated user profile
        createUserProfile(savedUser);
        
        // For testing: automatically verify new users
        savedUser.setIsEmailVerified(true);
        savedUser.setIsPhoneVerified(true);
        savedUser = userRepository.save(savedUser);
        
        // Send welcome notification
        notificationService.sendWelcomeNotification(savedUser);
        
        return savedUser;
    }
    
    /**
     * Authenticate user by email and password
     */
    public Optional<User> authenticateUser(String email, String password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.getIsActive() && passwordEncoder.matches(password, user.getPassword())) {
                // Update last login
                user.setLastLogin(LocalDateTime.now());
                userRepository.save(user);
                return Optional.of(user);
            }
        }
        
        return Optional.empty();
    }
    
    /**
     * Verify user's email address
     */
    public boolean verifyEmail(Long userId, String verificationToken) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // In a real implementation, you'd validate the verification token
            user.setIsEmailVerified(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Send email verification success notification
            notificationService.sendEmailVerificationSuccessNotification(user);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Verify user's phone number
     */
    public boolean verifyPhoneNumber(Long userId, String verificationCode) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // In a real implementation, you'd validate the verification code
            user.setIsPhoneVerified(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Send phone verification success notification
            notificationService.sendPhoneVerificationSuccessNotification(user);
            
            return true;
        }
        
        return false;
    }
    
    // User Profile Management
    
    /**
     * Create user profile for a new user
     */
    private UserProfile createUserProfile(User user) {
        UserProfile profile = new UserProfile();
        profile.setUser(user);
        profile.setCreatedAt(LocalDateTime.now());
        profile.setUpdatedAt(LocalDateTime.now());
        
        return userProfileRepository.save(profile);
    }
    
    /**
     * Update user basic information
     */
    public User updateUserBasicInfo(Long userId, String firstName, String lastName, String phoneNumber) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Check phone number uniqueness if changed
            if (!user.getPhoneNumber().equals(phoneNumber) && userRepository.existsByPhoneNumber(phoneNumber)) {
                throw new IllegalArgumentException("Phone number already exists");
            }
            
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPhoneNumber(phoneNumber);
            user.setUpdatedAt(LocalDateTime.now());
            
            // Reset phone verification if phone number changed
            if (!user.getPhoneNumber().equals(phoneNumber)) {
                user.setIsPhoneVerified(false);
            }
            
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found");
    }
    
    /**
     * Update user password
     */
    public boolean updatePassword(Long userId, String currentPassword, String newPassword) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            
            // Verify current password
            if (passwordEncoder.matches(currentPassword, user.getPassword())) {
                user.setPassword(passwordEncoder.encode(newPassword));
                user.setUpdatedAt(LocalDateTime.now());
                userRepository.save(user);
                
                // Send password change notification
                notificationService.sendPasswordChangeNotification(user);
                
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Update user theme preference
     */
    public User updateTheme(Long userId, User.UserTheme theme) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setTheme(theme);
            user.setUpdatedAt(LocalDateTime.now());
            return userRepository.save(user);
        }
        
        throw new IllegalArgumentException("User not found");
    }
    
    /**
     * Deactivate user account
     */
    public boolean deactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(false);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Send account deactivation notification
            notificationService.sendAccountDeactivationNotification(user);
            
            return true;
        }
        
        return false;
    }
    
    /**
     * Reactivate user account
     */
    public boolean reactivateUser(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setIsActive(true);
            user.setUpdatedAt(LocalDateTime.now());
            userRepository.save(user);
            
            // Send account reactivation notification
            notificationService.sendAccountReactivationNotification(user);
            
            return true;
        }
        
        return false;
    }
    
    // Rating Management
    
    /**
     * Update user rating as poster
     */
    public void updateRatingAsPoster(Long userId, Double newRating) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.updateRatingAsPoster(newRating);
            userRepository.save(user);
        }
    }
    
    /**
     * Update user rating as fulfiller
     */
    public void updateRatingAsFulfiller(Long userId, Double newRating) {
        Optional<User> userOpt = userRepository.findById(userId);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.updateRatingAsFulfiller(newRating);
            userRepository.save(user);
        }
    }
    
    // Search and Discovery
    
    /**
     * Search users by name or email
     */
    @Transactional(readOnly = true)
    public List<User> searchUsers(String searchTerm) {
        return userRepository.searchUsers(searchTerm);
    }
    
    /**
     * Get top rated posters
     */
    @Transactional(readOnly = true)
    public List<User> getTopRatedPosters(Double minRating) {
        return userRepository.findTopRatedPosters(minRating);
    }
    
    /**
     * Get top rated fulfillers
     */
    @Transactional(readOnly = true)
    public List<User> getTopRatedFulfillers(Double minRating) {
        return userRepository.findTopRatedFulfillers(minRating);
    }
    
    /**
     * Get recently active users
     */
    @Transactional(readOnly = true)
    public List<User> getRecentlyActiveUsers(LocalDateTime since) {
        return userRepository.findRecentlyActiveUsers(since);
    }
    
    // Utility Methods
    
    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long userId) {
        return userRepository.findById(userId);
    }
    
    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Check if user is fully verified
     */
    @Transactional(readOnly = true)
    public boolean isUserFullyVerified(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::isVerified).orElse(false);
    }
    
    /**
     * Get user's overall rating
     */
    @Transactional(readOnly = true)
    public Double getUserOverallRating(Long userId) {
        Optional<User> userOpt = userRepository.findById(userId);
        return userOpt.map(User::getOverallRating).orElse(0.0);
    }
    
    // Analytics Methods
    
    /**
     * Get total user count
     */
    @Transactional(readOnly = true)
    public Long getTotalUserCount() {
        return userRepository.count();
    }
    
    /**
     * Get active user count
     */
    @Transactional(readOnly = true)
    public Long getActiveUserCount() {
        return (long) userRepository.findByIsActiveTrue().size();
    }
    
    /**
     * Get verified user count
     */
    @Transactional(readOnly = true)
    public Long getVerifiedUserCount() {
        return userRepository.countVerifiedUsers();
    }
    
    /**
     * Get users registered between dates
     */
    @Transactional(readOnly = true)
    public Long getUsersRegisteredBetween(LocalDateTime startDate, LocalDateTime endDate) {
        return userRepository.countUsersRegisteredBetween(startDate, endDate);
    }
    
    /**
     * Get average user rating across platform
     */
    @Transactional(readOnly = true)
    public Double getAverageUserRating() {
        return userRepository.getAverageUserRating();
    }
    
    /**
     * Check if the authenticated user matches the provided user ID
     */
    public boolean isCurrentUser(Long userId, org.springframework.security.core.Authentication authentication) {
        if (authentication == null || authentication.getName() == null) {
            return false;
        }
        
        String email = authentication.getName();
        Optional<User> userOpt = userRepository.findByEmail(email);
        
        return userOpt.map(user -> user.getId().equals(userId)).orElse(false);
    }
}
