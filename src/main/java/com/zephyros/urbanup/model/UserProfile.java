package com.zephyros.urbanup.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_profiles")
public class UserProfile {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private User user;
    
    // Personal Information
    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;
    
    @Enumerated(EnumType.STRING)
    private Gender gender;
    
    @Column(columnDefinition = "TEXT")
    private String bio;
    
    // Address Information
    @Column(name = "address_line1")
    private String addressLine1;
    
    @Column(name = "address_line2")
    private String addressLine2;
    
    private String city;
    private String state;
    private String country;
    
    @Column(name = "postal_code")
    private String postalCode;
    
    // Location preferences
    @Column(name = "preferred_radius_km")
    private Integer preferredRadiusKm = 10; // Default 10km radius
    
    @Column(name = "home_latitude")
    private Double homeLatitude;
    
    @Column(name = "home_longitude")
    private Double homeLongitude;
    
    // Skills and Preferences
    @ElementCollection
    @CollectionTable(name = "user_skills", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "skill")
    @JsonIgnore
    private List<Task.TaskCategory> skills = new ArrayList<>();
    
    @ElementCollection
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "interest")
    @JsonIgnore
    private List<Task.TaskCategory> interests = new ArrayList<>();
    
    // KYC and Verification
    @Enumerated(EnumType.STRING)
    @Column(name = "kyc_status")
    private KYCStatus kycStatus = KYCStatus.NOT_STARTED;
    
    @Column(name = "government_id_type")
    private String governmentIdType;
    
    @Column(name = "government_id_number")
    private String governmentIdNumber;
    
    @Column(name = "government_id_verified")
    private Boolean governmentIdVerified = false;
    
    @Column(name = "phone_verified")
    private Boolean phoneVerified = false;
    
    @Column(name = "email_verified")
    private Boolean emailVerified = false;
    
    @Column(name = "address_verified")
    private Boolean addressVerified = false;
    
    @Column(name = "background_check_status")
    @Enumerated(EnumType.STRING)
    private BackgroundCheckStatus backgroundCheckStatus = BackgroundCheckStatus.NOT_REQUESTED;
    
    // Badges and Achievements
    @ElementCollection
    @CollectionTable(name = "user_badges", joinColumns = @JoinColumn(name = "user_profile_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "badge")
    @JsonIgnore
    private List<UserBadge> badges = new ArrayList<>();
    
    // Activity Stats
    @Column(name = "tasks_posted")
    private Integer tasksPosted = 0;
    
    @Column(name = "tasks_completed")
    private Integer tasksCompleted = 0;
    
    @Column(name = "total_earnings")
    private Double totalEarnings = 0.0;
    
    @Column(name = "total_spent")
    private Double totalSpent = 0.0;
    
    // Preferences
    @Column(name = "notification_preferences", columnDefinition = "TEXT")
    private String notificationPreferences; // JSON string
    
    @Column(name = "privacy_settings", columnDefinition = "TEXT")
    private String privacySettings; // JSON string
    
    @Column(name = "availability_status")
    @Enumerated(EnumType.STRING)
    private AvailabilityStatus availabilityStatus = AvailabilityStatus.AVAILABLE;
    
    @Column(name = "last_location_update")
    private LocalDateTime lastLocationUpdate;
    
    // Emergency Contact
    @Column(name = "emergency_contact_name")
    private String emergencyContactName;
    
    @Column(name = "emergency_contact_phone")
    private String emergencyContactPhone;
    
    @Column(name = "emergency_contact_relationship")
    private String emergencyContactRelationship;
    
    // Audit fields
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    // Enums
    public enum Gender {
        MALE("Male"),
        FEMALE("Female"),
        NON_BINARY("Non-binary"),
        PREFER_NOT_TO_SAY("Prefer not to say");
        
        private final String displayName;
        
        Gender(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum KYCStatus {
        NOT_STARTED("Not Started"),
        IN_PROGRESS("In Progress"),
        SUBMITTED("Submitted"),
        VERIFIED("Verified"),
        REJECTED("Rejected");
        
        private final String displayName;
        
        KYCStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum BackgroundCheckStatus {
        NOT_REQUESTED("Not Requested"),
        REQUESTED("Requested"),
        IN_PROGRESS("In Progress"),
        PASSED("Passed"),
        FAILED("Failed");
        
        private final String displayName;
        
        BackgroundCheckStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum UserBadge {
        TOP_RATED_POSTER("Top Rated Poster"),
        TOP_RATED_FULFILLER("Top Rated Fulfiller"),
        QUICK_RESPONDER("Quick Responder"),
        RELIABLE_WORKER("Reliable Worker"),
        VERIFIED_USER("Verified User"),
        POWER_USER("Power User"),
        HELPFUL_REVIEWER("Helpful Reviewer"),
        EARLY_ADOPTER("Early Adopter"),
        COMMUNITY_CONTRIBUTOR("Community Contributor"),
        EXPERT_CLEANER("Expert Cleaner"),
        EXPERT_DELIVERY("Expert Delivery"),
        EXPERT_HANDYMAN("Expert Handyman");
        
        private final String displayName;
        
        UserBadge(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    public enum AvailabilityStatus {
        AVAILABLE("Available"),
        BUSY("Busy"),
        AWAY("Away"),
        INVISIBLE("Invisible");
        
        private final String displayName;
        
        AvailabilityStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // Constructors
    public UserProfile() {}
    
    public UserProfile(User user) {
        this.user = user;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    
    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }
    
    public Gender getGender() { return gender; }
    public void setGender(Gender gender) { this.gender = gender; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getAddressLine1() { return addressLine1; }
    public void setAddressLine1(String addressLine1) { this.addressLine1 = addressLine1; }
    
    public String getAddressLine2() { return addressLine2; }
    public void setAddressLine2(String addressLine2) { this.addressLine2 = addressLine2; }
    
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }
    
    public String getPostalCode() { return postalCode; }
    public void setPostalCode(String postalCode) { this.postalCode = postalCode; }
    
    public Integer getPreferredRadiusKm() { return preferredRadiusKm; }
    public void setPreferredRadiusKm(Integer preferredRadiusKm) { this.preferredRadiusKm = preferredRadiusKm; }
    
    public Double getHomeLatitude() { return homeLatitude; }
    public void setHomeLatitude(Double homeLatitude) { this.homeLatitude = homeLatitude; }
    
    public Double getHomeLongitude() { return homeLongitude; }
    public void setHomeLongitude(Double homeLongitude) { this.homeLongitude = homeLongitude; }
    
    public List<Task.TaskCategory> getSkills() { return skills; }
    public void setSkills(List<Task.TaskCategory> skills) { this.skills = skills; }
    
    public List<Task.TaskCategory> getInterests() { return interests; }
    public void setInterests(List<Task.TaskCategory> interests) { this.interests = interests; }
    
    public KYCStatus getKycStatus() { return kycStatus; }
    public void setKycStatus(KYCStatus kycStatus) { this.kycStatus = kycStatus; }
    
    public String getGovernmentIdType() { return governmentIdType; }
    public void setGovernmentIdType(String governmentIdType) { this.governmentIdType = governmentIdType; }
    
    public String getGovernmentIdNumber() { return governmentIdNumber; }
    public void setGovernmentIdNumber(String governmentIdNumber) { this.governmentIdNumber = governmentIdNumber; }
    
    public Boolean getGovernmentIdVerified() { return governmentIdVerified; }
    public void setGovernmentIdVerified(Boolean governmentIdVerified) { this.governmentIdVerified = governmentIdVerified; }
    
    public Boolean getPhoneVerified() { return phoneVerified; }
    public void setPhoneVerified(Boolean phoneVerified) { this.phoneVerified = phoneVerified; }
    
    public Boolean getEmailVerified() { return emailVerified; }
    public void setEmailVerified(Boolean emailVerified) { this.emailVerified = emailVerified; }
    
    public Boolean getAddressVerified() { return addressVerified; }
    public void setAddressVerified(Boolean addressVerified) { this.addressVerified = addressVerified; }
    
    public BackgroundCheckStatus getBackgroundCheckStatus() { return backgroundCheckStatus; }
    public void setBackgroundCheckStatus(BackgroundCheckStatus backgroundCheckStatus) { this.backgroundCheckStatus = backgroundCheckStatus; }
    
    public List<UserBadge> getBadges() { return badges; }
    public void setBadges(List<UserBadge> badges) { this.badges = badges; }
    
    public Integer getTasksPosted() { return tasksPosted; }
    public void setTasksPosted(Integer tasksPosted) { this.tasksPosted = tasksPosted; }
    
    public Integer getTasksCompleted() { return tasksCompleted; }
    public void setTasksCompleted(Integer tasksCompleted) { this.tasksCompleted = tasksCompleted; }
    
    public Double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(Double totalEarnings) { this.totalEarnings = totalEarnings; }
    
    public Double getTotalSpent() { return totalSpent; }
    public void setTotalSpent(Double totalSpent) { this.totalSpent = totalSpent; }
    
    public String getNotificationPreferences() { return notificationPreferences; }
    public void setNotificationPreferences(String notificationPreferences) { this.notificationPreferences = notificationPreferences; }
    
    public String getPrivacySettings() { return privacySettings; }
    public void setPrivacySettings(String privacySettings) { this.privacySettings = privacySettings; }
    
    public AvailabilityStatus getAvailabilityStatus() { return availabilityStatus; }
    public void setAvailabilityStatus(AvailabilityStatus availabilityStatus) { this.availabilityStatus = availabilityStatus; }
    
    public LocalDateTime getLastLocationUpdate() { return lastLocationUpdate; }
    public void setLastLocationUpdate(LocalDateTime lastLocationUpdate) { this.lastLocationUpdate = lastLocationUpdate; }
    
    public String getEmergencyContactName() { return emergencyContactName; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    
    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    // Helper methods
    public boolean isVerified() {
        return phoneVerified && emailVerified && kycStatus == KYCStatus.VERIFIED;
    }
    
    public boolean isFullyVerified() {
        return isVerified() && addressVerified && governmentIdVerified;
    }
    
    public void addSkill(Task.TaskCategory skill) {
        if (this.skills == null) {
            this.skills = new ArrayList<>();
        }
        if (!this.skills.contains(skill)) {
            this.skills.add(skill);
        }
    }
    
    public void addInterest(Task.TaskCategory interest) {
        if (this.interests == null) {
            this.interests = new ArrayList<>();
        }
        if (!this.interests.contains(interest)) {
            this.interests.add(interest);
        }
    }
    
    public void addBadge(UserBadge badge) {
        if (this.badges == null) {
            this.badges = new ArrayList<>();
        }
        if (!this.badges.contains(badge)) {
            this.badges.add(badge);
        }
    }
    
    public void incrementTasksPosted() {
        this.tasksPosted = (this.tasksPosted != null ? this.tasksPosted : 0) + 1;
    }
    
    public void incrementTasksCompleted() {
        this.tasksCompleted = (this.tasksCompleted != null ? this.tasksCompleted : 0) + 1;
    }
    
    public void addEarnings(Double amount) {
        this.totalEarnings = (this.totalEarnings != null ? this.totalEarnings : 0.0) + amount;
    }
    
    public void addSpending(Double amount) {
        this.totalSpent = (this.totalSpent != null ? this.totalSpent : 0.0) + amount;
    }
    
    public String getFullAddress() {
        StringBuilder address = new StringBuilder();
        if (addressLine1 != null) address.append(addressLine1);
        if (addressLine2 != null) address.append(", ").append(addressLine2);
        if (city != null) address.append(", ").append(city);
        if (state != null) address.append(", ").append(state);
        if (postalCode != null) address.append(" ").append(postalCode);
        if (country != null) address.append(", ").append(country);
        return address.toString();
    }
    
    @PreUpdate
    public void setLastModifiedDate() {
        this.updatedAt = LocalDateTime.now();
    }
}
