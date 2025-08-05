package com.zephyros.urbanup.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class CurrentUserService {

    /**
     * Get the currently authenticated user's principal
     */
    public UserPrincipal getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            return (UserPrincipal) authentication.getPrincipal();
        }
        
        return null;
    }

    /**
     * Get the currently authenticated user's ID
     */
    public Long getCurrentUserId() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getId() : null;
    }

    /**
     * Get the currently authenticated user's email
     */
    public String getCurrentUserEmail() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null ? userPrincipal.getEmail() : null;
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated() 
               && !(authentication.getPrincipal() instanceof String); // "anonymousUser" is a String
    }

    /**
     * Check if current user is verified
     */
    public boolean isCurrentUserVerified() {
        UserPrincipal userPrincipal = getCurrentUser();
        return userPrincipal != null && userPrincipal.getIsEmailVerified() && userPrincipal.getIsPhoneVerified();
    }
}
