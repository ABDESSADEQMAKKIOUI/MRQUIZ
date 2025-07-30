package com.example.mrquiz.service.auth;

import com.example.mrquiz.dto.auth.*;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.enums.UserStatus;
import com.example.mrquiz.repository.auth.UserRepository;
import com.example.mrquiz.service.auth.CustomUserDetailsService.CustomUserPrincipal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    @Transactional
    public UserResponseDto signup(SignupRequestDto request) {
        log.info("Attempting to register user with email: {}", request.getEmail());

        // Validate passwords match
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Passwords do not match");
        }

        // Validate terms acceptance
        if (!Boolean.TRUE.equals(request.getAcceptTerms())) {
            throw new IllegalArgumentException("You must accept the terms and conditions");
        }

        // Check if user already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("User with this email already exists");
        }

        if (request.getUsername() != null && userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException("Username is already taken");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setUsername(request.getUsername());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFirstName(request.getFirstName());
        user.setLastName(request.getLastName());
        user.setRole(request.getRole());
        user.setStatus(UserStatus.PENDING_VERIFICATION);
        user.setPhone(request.getPhone());
        user.setTimezone(request.getTimezone());
        user.setLanguage(request.getLanguage());
        user.setEmailVerified(false);
        user.setLastPasswordChange(LocalDateTime.now());

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with ID: {}", savedUser.getId());

        // Convert to response DTO
        return convertToUserResponseDto(savedUser);
    }

    @Transactional
    public LoginResponseDto login(LoginRequestDto request) {
        log.info("Attempting to authenticate user with email: {}", request.getEmail());

        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            CustomUserPrincipal userPrincipal = (CustomUserPrincipal) authentication.getPrincipal();
            User user = userPrincipal.getUser();

            // Check if account is active
            if (user.getStatus() != UserStatus.ACTIVE) {
                throw new DisabledException("Account is not active. Status: " + user.getStatus());
            }

            // Generate tokens
            String accessToken = jwtService.generateAccessToken(userPrincipal, user.getId(), user.getRole().name());
            String refreshToken = jwtService.generateRefreshToken(userPrincipal, user.getId());

            // Update last login
            user.setLastLogin(LocalDateTime.now());
            user.setLastActivity(LocalDateTime.now());
            user.setFailedLoginAttempts(0); // Reset failed attempts on successful login
            userRepository.save(user);

            log.info("User authenticated successfully: {}", user.getEmail());

            return LoginResponseDto.builder()
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenType("Bearer")
                    .expiresIn(jwtService.getAccessTokenExpiration() / 1000) // Convert to seconds
                    .userId(user.getId())
                    .email(user.getEmail())
                    .username(user.getUsername())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(user.getRole())
                    .status(user.getStatus())
                    .emailVerified(user.getEmailVerified())
                    .profileImageId(user.getProfileImage() != null ? user.getProfileImage().getId() : null)
                    .lastLogin(user.getLastLogin())
                    .build();

        } catch (BadCredentialsException e) {
            handleFailedLogin(request.getEmail());
            throw new BadCredentialsException("Invalid email or password");
        }
    }

    @Transactional
    public LoginResponseDto refreshToken(RefreshTokenRequestDto request) {
        String refreshToken = request.getRefreshToken();

        try {
            // Validate refresh token
            if (!jwtService.isRefreshToken(refreshToken)) {
                throw new IllegalArgumentException("Invalid refresh token type");
            }

            String email = jwtService.extractUsername(refreshToken);
            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            if (jwtService.isTokenValid(refreshToken, userDetails)) {
                CustomUserPrincipal userPrincipal = (CustomUserPrincipal) userDetails;
                User user = userPrincipal.getUser();

                // Generate new access token
                String newAccessToken = jwtService.generateAccessToken(userDetails, user.getId(), user.getRole().name());

                return LoginResponseDto.builder()
                        .accessToken(newAccessToken)
                        .refreshToken(refreshToken) // Keep the same refresh token
                        .tokenType("Bearer")
                        .expiresIn(jwtService.getAccessTokenExpiration() / 1000)
                        .userId(user.getId())
                        .email(user.getEmail())
                        .username(user.getUsername())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRole())
                        .status(user.getStatus())
                        .emailVerified(user.getEmailVerified())
                        .profileImageId(user.getProfileImage() != null ? user.getProfileImage().getId() : null)
                        .lastLogin(user.getLastLogin())
                        .build();
            } else {
                throw new IllegalArgumentException("Invalid or expired refresh token");
            }
        } catch (Exception e) {
            log.error("Error refreshing token: {}", e.getMessage());
            throw new IllegalArgumentException("Invalid refresh token");
        }
    }

    private void handleFailedLogin(String email) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            // Lock account after 5 failed attempts for 30 minutes
            if (attempts >= 5) {
                user.setLockedUntil(LocalDateTime.now().plusMinutes(30));
                log.warn("Account locked due to too many failed login attempts: {}", email);
            }

            userRepository.save(user);
        }
    }

    private UserResponseDto convertToUserResponseDto(User user) {
        UserResponseDto dto = new UserResponseDto();
        dto.setId(user.getId());
        dto.setEmail(user.getEmail());
        dto.setUsername(user.getUsername());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());
        dto.setEmailVerified(user.getEmailVerified());
        dto.setPhone(user.getPhone());
        dto.setPhoneVerified(user.getPhoneVerified());
        dto.setMfaEnabled(user.getMfaEnabled());
        dto.setProfileImageId(user.getProfileImage() != null ? user.getProfileImage().getId() : null);
        dto.setTimezone(user.getTimezone());
        dto.setLanguage(user.getLanguage());
        dto.setFailedLoginAttempts(user.getFailedLoginAttempts());
        dto.setLockedUntil(user.getLockedUntil());
        dto.setMustChangePassword(user.getMustChangePassword());
        dto.setLastPasswordChange(user.getLastPasswordChange());
        dto.setMetadata(user.getMetadata());
        dto.setPreferences(user.getPreferences());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setLastLogin(user.getLastLogin());
        dto.setLastActivity(user.getLastActivity());
        return dto;
    }
}