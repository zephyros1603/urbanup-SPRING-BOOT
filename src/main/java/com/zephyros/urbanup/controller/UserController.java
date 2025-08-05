package com.zephyros.urbanup.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.zephyros.urbanup.dto.ApiResponse;
import com.zephyros.urbanup.dto.UserProfileUpdateDto;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.service.UserService;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    /**
     * Get user profile
     */
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> getUserProfile(@PathVariable Long userId) {
        try {
            Optional<User> userOpt = userService.getUserById(userId);
            
            if (userOpt.isPresent()) {
                ApiResponse<User> response = new ApiResponse<>(true, "User found", userOpt.get());
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.notFound().build();
            }
            
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(false, "Failed to retrieve user", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Update user profile
     */
    @PutMapping("/{userId}")
    public ResponseEntity<ApiResponse<User>> updateUserProfile(
            @PathVariable Long userId, 
            @RequestBody UserProfileUpdateDto updateDto) {
        try {
            User updatedUser = userService.updateUserBasicInfo(
                userId,
                updateDto.getFirstName(),
                updateDto.getLastName(),
                updateDto.getPhoneNumber()
            );
            
            ApiResponse<User> response = new ApiResponse<>(true, "Profile updated successfully", updatedUser);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<User> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(false, "Profile update failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Email verification
     */
    @PostMapping("/{userId}/verify-email")
    public ResponseEntity<ApiResponse<String>> verifyEmail(
            @PathVariable Long userId,
            @RequestParam String verificationCode) {
        try {
            boolean verified = userService.verifyEmail(userId, verificationCode);
            
            if (verified) {
                ApiResponse<String> response = new ApiResponse<>(true, "Email verified successfully", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(false, "Invalid verification code", null);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Email verification failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Phone verification
     */
    @PostMapping("/{userId}/verify-phone")
    public ResponseEntity<ApiResponse<String>> verifyPhone(
            @PathVariable Long userId,
            @RequestParam String verificationCode) {
        try {
            boolean verified = userService.verifyPhoneNumber(userId, verificationCode);
            
            if (verified) {
                ApiResponse<String> response = new ApiResponse<>(true, "Phone verified successfully", null);
                return ResponseEntity.ok(response);
            } else {
                ApiResponse<String> response = new ApiResponse<>(false, "Invalid verification code", null);
                return ResponseEntity.badRequest().body(response);
            }
            
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Phone verification failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Change user theme
     */
    @PutMapping("/{userId}/theme")
    public ResponseEntity<ApiResponse<User>> changeTheme(
            @PathVariable Long userId,
            @RequestParam User.UserTheme theme) {
        try {
            User updatedUser = userService.updateTheme(userId, theme);
            
            ApiResponse<User> response = new ApiResponse<>(true, "Theme updated successfully", updatedUser);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<User> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<User> response = new ApiResponse<>(false, "Theme update failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Deactivate user account
     */
    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<ApiResponse<String>> deactivateAccount(@PathVariable Long userId) {
        try {
            userService.deactivateUser(userId);
            
            ApiResponse<String> response = new ApiResponse<>(true, "Account deactivated successfully", null);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Account deactivation failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Reactivate user account
     */
    @PutMapping("/{userId}/reactivate")
    public ResponseEntity<ApiResponse<String>> reactivateAccount(@PathVariable Long userId) {
        try {
            userService.reactivateUser(userId);
            
            ApiResponse<String> response = new ApiResponse<>(true, "Account reactivated successfully", null);
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            ApiResponse<String> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<String> response = new ApiResponse<>(false, "Account reactivation failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Search users
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<User>>> searchUsers(@RequestParam String searchTerm) {
        try {
            List<User> users = userService.searchUsers(searchTerm);
            
            ApiResponse<List<User>> response = new ApiResponse<>(true, "Users found", users);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<User>> response = new ApiResponse<>(false, "Search failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get top rated posters
     */
    @GetMapping("/top-posters")
    public ResponseEntity<ApiResponse<List<User>>> getTopRatedPosters(
            @RequestParam(defaultValue = "4.0") Double minRating) {
        try {
            List<User> users = userService.getTopRatedPosters(minRating);
            
            ApiResponse<List<User>> response = new ApiResponse<>(true, "Top rated posters retrieved", users);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<User>> response = new ApiResponse<>(false, "Failed to retrieve top posters", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Get top rated fulfillers
     */
    @GetMapping("/top-fulfillers")
    public ResponseEntity<ApiResponse<List<User>>> getTopRatedFulfillers(
            @RequestParam(defaultValue = "4.0") Double minRating) {
        try {
            List<User> users = userService.getTopRatedFulfillers(minRating);
            
            ApiResponse<List<User>> response = new ApiResponse<>(true, "Top rated fulfillers retrieved", users);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            ApiResponse<List<User>> response = new ApiResponse<>(false, "Failed to retrieve top fulfillers", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
