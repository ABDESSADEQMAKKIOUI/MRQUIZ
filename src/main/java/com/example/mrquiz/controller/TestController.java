package com.example.mrquiz.controller;

import com.example.mrquiz.service.auth.CustomUserDetailsService.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    @GetMapping("/public")
    public ResponseEntity<?> publicEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "This is a public endpoint - no authentication required");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/protected")
    public ResponseEntity<?> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "This is a protected endpoint - authentication required");
        response.put("user", authentication.getName());
        response.put("authorities", authentication.getAuthorities());
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user-info")
    public ResponseEntity<?> getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication.getPrincipal() instanceof CustomUserPrincipal userPrincipal) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Current user information");
            response.put("user", Map.of(
                "id", userPrincipal.getUser().getId(),
                "email", userPrincipal.getUser().getEmail(),
                "firstName", userPrincipal.getUser().getFirstName(),
                "lastName", userPrincipal.getUser().getLastName(),
                "role", userPrincipal.getUser().getRole(),
                "status", userPrincipal.getUser().getStatus()
            ));
            response.put("timestamp", System.currentTimeMillis());
            
            return ResponseEntity.ok(response);
        }
        
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "Unable to retrieve user information");
        return ResponseEntity.badRequest().body(errorResponse);
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> adminEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "This is an admin-only endpoint");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/teacher")
    @PreAuthorize("hasAnyRole('TEACHER', 'ADMIN')")
    public ResponseEntity<?> teacherEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "This endpoint is accessible by teachers and admins");
        response.put("timestamp", System.currentTimeMillis());
        
        return ResponseEntity.ok(response);
    }
}