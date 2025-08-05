package com.zephyros.urbanup.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.zephyros.urbanup.dto.ApiResponse;
import com.zephyros.urbanup.dto.JwtAuthenticationResponse;
import com.zephyros.urbanup.dto.UserLoginDto;
import com.zephyros.urbanup.dto.UserRegistrationDto;
import com.zephyros.urbanup.model.User;
import com.zephyros.urbanup.security.JwtUtil;
import com.zephyros.urbanup.security.UserPrincipal;
import com.zephyros.urbanup.service.UserService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * User Registration
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> registerUser(
            @Valid @RequestBody UserRegistrationDto signUpRequest) {
        try {
            User user = userService.registerUser(
                signUpRequest.getFirstName(),
                signUpRequest.getLastName(),
                signUpRequest.getEmail(),
                signUpRequest.getPhoneNumber(),
                signUpRequest.getPassword(),
                "WEB"
            );

            // Generate JWT token for the new user
            String jwt = jwtUtil.generateToken(user.getEmail(), user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

            JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse(
                jwt, refreshToken, "Bearer", user);

            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(
                true, "User registered successfully", jwtResponse);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);

        } catch (IllegalArgumentException e) {
            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(false, e.getMessage(), null);
            return ResponseEntity.badRequest().body(response);
        } catch (Exception e) {
            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(false, "Registration failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * User Login
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> authenticateUser(
            @Valid @RequestBody UserLoginDto loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getEmail(),
                    loginRequest.getPassword()
                )
            );

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            
            // Get the full user object
            Optional<User> userOpt = userService.getUserByEmail(userPrincipal.getEmail());
            if (userOpt.isEmpty()) {
                ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(false, "User not found", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            User user = userOpt.get();
            
            // Generate JWT tokens
            String jwt = jwtUtil.generateToken(user.getEmail(), user.getId());
            String refreshToken = jwtUtil.generateRefreshToken(user.getEmail(), user.getId());

            JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse(
                jwt, refreshToken, "Bearer", user);

            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(
                true, "Login successful", jwtResponse);
            
            return ResponseEntity.ok(response);

        } catch (BadCredentialsException e) {
            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(false, "Invalid credentials", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        } catch (Exception e) {
            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(false, "Login failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Refresh JWT Token
     */
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<JwtAuthenticationResponse>> refreshToken(
            @RequestBody String refreshToken) {
        try {
            if (jwtUtil.validateToken(refreshToken) && jwtUtil.isRefreshToken(refreshToken)) {
                String email = jwtUtil.extractUsername(refreshToken);
                Long userId = jwtUtil.extractUserId(refreshToken);
                
                Optional<User> userOpt = userService.getUserByEmail(email);
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    String newJwt = jwtUtil.generateToken(email, userId);
                    String newRefreshToken = jwtUtil.generateRefreshToken(email, userId);
                    
                    JwtAuthenticationResponse jwtResponse = new JwtAuthenticationResponse(
                        newJwt, newRefreshToken, "Bearer", user);
                    
                    ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(
                        true, "Token refreshed successfully", jwtResponse);
                    
                    return ResponseEntity.ok(response);
                }
            }
            
            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(false, "Invalid refresh token", null);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            
        } catch (Exception e) {
            ApiResponse<JwtAuthenticationResponse> response = new ApiResponse<>(false, "Token refresh failed", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
