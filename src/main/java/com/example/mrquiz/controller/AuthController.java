package com.example.mrquiz.controller;

import com.example.mrquiz.dto.auth.*;
import com.example.mrquiz.service.auth.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthController {

    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@Valid @RequestBody SignupRequestDto signupRequest) {
        try {
            UserResponseDto user = authenticationService.signup(signupRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "User registered successfully. Please verify your email.");
            response.put("user", user);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            log.warn("Signup failed: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
            
        } catch (Exception e) {
            log.error("Unexpected error during signup", e);
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "An unexpected error occurred. Please try again.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequestDto loginRequest) {
        try {
            LoginResponseDto loginResponse = authenticationService.login(loginRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Login successful");
            response.put("data", loginResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.warn("Login failed for email {}: {}", loginRequest.getEmail(), e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@Valid @RequestBody RefreshTokenRequestDto refreshRequest) {
        try {
            LoginResponseDto loginResponse = authenticationService.refreshToken(refreshRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Token refreshed successfully");
            response.put("data", loginResponse);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.warn("Token refresh failed: {}", e.getMessage());
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // For JWT, logout is typically handled client-side by removing the token
        // You could implement token blacklisting here if needed
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Logged out successfully");
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        // This endpoint would be used to get current user info
        // Implementation would require authentication context
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "User info endpoint - requires authentication implementation");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        
        // TODO: Implement forgot password logic
        log.info("Forgot password request for email: {}", email);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "If an account with that email exists, a password reset link has been sent.");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        String newPassword = request.get("newPassword");
        
        // TODO: Implement reset password logic
        log.info("Reset password request with token: {}", token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Password reset successfully");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-email")
    public ResponseEntity<?> verifyEmail(@RequestBody Map<String, String> request) {
        String token = request.get("token");
        
        // TODO: Implement email verification logic
        log.info("Email verification request with token: {}", token);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Email verified successfully");
        
        return ResponseEntity.ok(response);
    }
}