package com.zephyros.urbanup.dto;

public class UserProfileUpdateDto {
    
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String bio;
    private String profilePictureUrl;
    
    public UserProfileUpdateDto() {}
    
    public UserProfileUpdateDto(String firstName, String lastName, String phoneNumber, String bio, String profilePictureUrl) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.bio = bio;
        this.profilePictureUrl = profilePictureUrl;
    }
    
    // Getters and setters
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    
    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
    
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
}
