package com.zephyros.urbanup.dto;

import com.zephyros.urbanup.model.User;

public class JwtAuthenticationResponse {
    
    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private User user;

    public JwtAuthenticationResponse() {}

    public JwtAuthenticationResponse(String accessToken, String refreshToken, String tokenType, User user) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.user = user;
    }

    // Getters and Setters
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
