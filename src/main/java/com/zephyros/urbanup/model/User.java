package com.zephyros.urbanup.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "users")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    @NotBlank(message = "Email is required")
    @Email(message = "Please provide a valid email")
    private String email;
    
    @Column(nullable = false)
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
    
    @Column(nullable = false)
    @NotBlank(message = "First name is required")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;
    
    @Column(nullable = false)
    @NotBlank(message = "Last name is required")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;
    
    @Column(unique = true)
    private String phoneNumber;
    
    private String profilePictureUrl;
    
    @Enumerated(EnumType.STRING)
    private UserTheme theme = UserTheme.LIGHT;
    
    // Rating fields
    private Double ratingAsPoster = 0.0;
    private Integer ratingsAsPostCount = 0;
    private Double ratingAsFulfiller = 0.0;
    private Integer ratingsAsFulfillerCount = 0;
    
    // Account status
    private Boolean isActive = true;
    private Boolean isEmailVerified = false;
    private Boolean isPhoneVerified = false;
    
    // Additional user information
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "account_created_from")
    private String accountCreatedFrom; // "mobile", "web"
    
    // FCM token for push notifications
    @Column(name = "fcm_token")
    private String fcmToken;
    
    // Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private UserProfile profile;
    
    @OneToMany(mappedBy = "poster", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Task> postedTasks = new ArrayList<>();
    
    @OneToMany(mappedBy = "fulfiller", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Task> fulfilledTasks = new ArrayList<>();
    
    @OneToMany(mappedBy = "reviewer", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> givenReviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "reviewee", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Review> receivedReviews = new ArrayList<>();
    
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Notification> notifications = new ArrayList<>();
    
    // Audit fields
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public enum UserTheme {
        LIGHT("Light"),
        DARK("Dark");
        
        private final String displayName;
        
        UserTheme(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    } // Constructors

    public User() {
    }

    public User(String email, String password, String firstName, String lastName) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    } // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setProfilePictureUrl(String profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public UserTheme getTheme() {
        return theme;
    }

    public void setTheme(UserTheme theme) {
        this.theme = theme;
    }

    public Double getRatingAsPoster() {
        return ratingAsPoster;
    }

    public void setRatingAsPoster(Double ratingAsPoster) {
        this.ratingAsPoster = ratingAsPoster;
    }

    public Integer getRatingsAsPostCount() {
        return ratingsAsPostCount;
    }

    public void setRatingsAsPostCount(Integer ratingsAsPostCount) {
        this.ratingsAsPostCount = ratingsAsPostCount;
    }

    public Double getRatingAsFulfiller() {
        return ratingAsFulfiller;
    }

    public void setRatingAsFulfiller(Double ratingAsFulfiller) {
        this.ratingAsFulfiller = ratingAsFulfiller;
    }

    public Integer getRatingsAsFulfillerCount() {
        return ratingsAsFulfillerCount;
    }

    public void setRatingsAsFulfillerCount(Integer ratingsAsFulfillerCount) {
        this.ratingsAsFulfillerCount = ratingsAsFulfillerCount;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsEmailVerified() {
        return isEmailVerified;
    }

    public void setIsEmailVerified(Boolean isEmailVerified) {
        this.isEmailVerified = isEmailVerified;
    }
    
    public Boolean getIsPhoneVerified() {
        return isPhoneVerified;
    }

    public void setIsPhoneVerified(Boolean isPhoneVerified) {
        this.isPhoneVerified = isPhoneVerified;
    }
    
    public LocalDateTime getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(LocalDateTime lastLogin) {
        this.lastLogin = lastLogin;
    }
    
    public String getAccountCreatedFrom() {
        return accountCreatedFrom;
    }

    public void setAccountCreatedFrom(String accountCreatedFrom) {
        this.accountCreatedFrom = accountCreatedFrom;
    }
    
    public String getFcmToken() {
        return fcmToken;
    }

    public void setFcmToken(String fcmToken) {
        this.fcmToken = fcmToken;
    }
    
    public UserProfile getProfile() {
        return profile;
    }

    public void setProfile(UserProfile profile) {
        this.profile = profile;
    }
    
    public List<Task> getPostedTasks() {
        return postedTasks;
    }

    public void setPostedTasks(List<Task> postedTasks) {
        this.postedTasks = postedTasks;
    }
    
    public List<Task> getFulfilledTasks() {
        return fulfilledTasks;
    }

    public void setFulfilledTasks(List<Task> fulfilledTasks) {
        this.fulfilledTasks = fulfilledTasks;
    }
    
    public List<Review> getGivenReviews() {
        return givenReviews;
    }

    public void setGivenReviews(List<Review> givenReviews) {
        this.givenReviews = givenReviews;
    }
    
    public List<Review> getReceivedReviews() {
        return receivedReviews;
    }

    public void setReceivedReviews(List<Review> receivedReviews) {
        this.receivedReviews = receivedReviews;
    }
    
    public List<Notification> getNotifications() {
        return notifications;
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    // Helper methods
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    public Double getOverallRating() {
        int totalRatings = (ratingsAsPostCount != null ? ratingsAsPostCount.intValue() : 0) + 
                          (ratingsAsFulfillerCount != null ? ratingsAsFulfillerCount.intValue() : 0);
        if (totalRatings == 0) return 0.0;
        
        double totalRatingSum = (ratingAsPoster != null ? ratingAsPoster * ratingsAsPostCount : 0.0) + 
                               (ratingAsFulfiller != null ? ratingAsFulfiller * ratingsAsFulfillerCount : 0.0);
        return totalRatingSum / totalRatings;
    }
    
    public void updateRatingAsPoster(Double newRating) {
        if (ratingsAsPostCount == null) ratingsAsPostCount = 0;
        if (ratingAsPoster == null) ratingAsPoster = 0.0;
        
        double totalRating = ratingAsPoster * ratingsAsPostCount + newRating;
        ratingsAsPostCount++;
        ratingAsPoster = totalRating / ratingsAsPostCount;
    }
    
    public void updateRatingAsFulfiller(Double newRating) {
        if (ratingsAsFulfillerCount == null) ratingsAsFulfillerCount = 0;
        if (ratingAsFulfiller == null) ratingAsFulfiller = 0.0;
        
        double totalRating = ratingAsFulfiller * ratingsAsFulfillerCount + newRating;
        ratingsAsFulfillerCount++;
        ratingAsFulfiller = totalRating / ratingsAsFulfillerCount;
    }
    
    public boolean isVerified() {
        return isEmailVerified && isPhoneVerified;
    }
    
    public void markLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    @PreUpdate
    public void setLastModifiedDate() {
        this.updatedAt = LocalDateTime.now();
    }
}
