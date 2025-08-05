package com.urbanup.controller;

import com.urbanup.dto.request.LoginRequest;
import com.urbanup.dto.request.RegisterRequest;
import com.urbanup.dto.response.AuthResponse;
import com.urbanup.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

public class AuthControllerTest {

    @InjectMocks
    private AuthController authController;

    @Mock
    private AuthService authService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testLogin() {
        LoginRequest loginRequest = new LoginRequest("test@example.com", "password");
        AuthResponse authResponse = new AuthResponse("token", "test@example.com");

        when(authService.login(loginRequest)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.login(loginRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }

    @Test
    public void testRegister() {
        RegisterRequest registerRequest = new RegisterRequest("test@example.com", "password");

        when(authService.register(registerRequest)).thenReturn("User registered successfully");

        ResponseEntity<String> response = authController.register(registerRequest);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals("User registered successfully", response.getBody());
    }

    @Test
    public void testRefreshToken() {
        String refreshToken = "refreshToken";
        AuthResponse authResponse = new AuthResponse("newToken", "test@example.com");

        when(authService.refreshToken(refreshToken)).thenReturn(authResponse);

        ResponseEntity<AuthResponse> response = authController.refreshToken(refreshToken);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(authResponse, response.getBody());
    }
}