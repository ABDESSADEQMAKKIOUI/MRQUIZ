package com.example.mrquiz.service.auth;

import com.example.mrquiz.dto.auth.*;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.entity.auth.UserProfile;
import com.example.mrquiz.enums.UserRole;
import com.example.mrquiz.enums.UserStatus;
import com.example.mrquiz.mapper.auth.UserMapper;
import com.example.mrquiz.repository.auth.UserRepository;
import com.example.mrquiz.repository.auth.UserProfileRepository;
import com.example.mrquiz.service.MappingService;
import com.example.mrquiz.service.notification.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private AuthTokenService authTokenService;

    // ============================================================================
    // BASIC USER OPERATIONS
    // ============================================================================

    public UserResponseDto createUser(UserCreateDto createDto) {
        // Check if email already exists
        if (userRepository.findByEmail(createDto.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        User user = mappingService.getUserMapper().toEntity(createDto);
        user = userRepository.save(user);

        // Send email verification if required
        if (!user.getEmailVerified()) {
            authTokenService.createEmailVerificationToken(user.getId());
        }

        return mappingService.getUserMapper().toResponseDto(user);
    }

    public UserResponseDto updateUser(UUID userId, UserUpdateDto updateDto) {
        User user = findUserById(userId);
        mappingService.getUserMapper().updateEntity(updateDto, user);
        user = userRepository.save(user);
        return mappingService.getUserMapper().toResponseDto(user);
    }

    public UserResponseDto getUserById(UUID userId) {
        User user = findUserById(userId);
        return mappingService.getUserMapper().toResponseDto(user);
    }

    public UserResponseDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return mappingService.getUserMapper().toResponseDto(user);
    }

    public void deleteUser(UUID userId) {
        User user = findUserById(userId);
        user.setStatus(UserStatus.INACTIVE);
        userRepository.save(user);
    }

    // ============================================================================
    // TEACHER-SPECIFIC OPERATIONS
    // ============================================================================

    public Page<UserResponseDto> getActiveTeachers(Pageable pageable) {
        Page<User> teachers = userRepository.findActiveTeachers(UserRole.TEACHER, pageable);
        return mappingService.getUserMapper().toResponseDtoPage(teachers);
    }

    public List<UserResponseDto> getVerifiedTeachers() {
        List<User> teachers = userRepository.findVerifiedTeachers();
        return mappingService.getUserMapper().toResponseDtoList(teachers);
    }

    public List<UserResponseDto> getTeachersBySpecialization(String specialization) {
        List<User> teachers = userRepository.findTeachersBySpecialization(specialization);
        return mappingService.getUserMapper().toResponseDtoList(teachers);
    }

    public Page<UserResponseDto> getTeachersWithSubscription(Pageable pageable) {
        Page<User> teachers = userRepository.findTeachersWithActiveSubscription(pageable);
        return mappingService.getUserMapper().toResponseDtoPage(teachers);
    }

    public List<UserResponseDto> getTopTeachersByRevenue(int limit) {
        List<User> teachers = userRepository.findTopTeachersByRevenue(limit);
        return mappingService.getUserMapper().toResponseDtoList(teachers);
    }

    public void verifyTeacher(UUID teacherId, UUID verifierId, Map<String, Object> verificationData) {
        User teacher = findUserById(teacherId);
        if (teacher.getRole() != UserRole.TEACHER) {
            throw new RuntimeException("User is not a teacher");
        }

        Map<String, Object> metadata = teacher.getMetadata();
        metadata.put("professionalVerified", true);
        metadata.put("verifiedBy", verifierId);
        metadata.put("verifiedAt", LocalDateTime.now());
        metadata.put("verificationData", verificationData);
        
        teacher.setMetadata(metadata);
        userRepository.save(teacher);

        // Send verification notification
        notificationService.sendTeacherVerificationNotification(teacherId);
    }

    // ============================================================================
    // USER PROFILE OPERATIONS
    // ============================================================================

    public UserProfileResponseDto createUserProfile(UserProfileCreateDto createDto) {
        // Check if profile already exists
        if (userProfileRepository.findByUserId(createDto.getUserId()).isPresent()) {
            throw new RuntimeException("User profile already exists");
        }

        UserProfile profile = mappingService.map(createDto, UserProfile.class);
        profile = userProfileRepository.save(profile);
        return mappingService.map(profile, UserProfileResponseDto.class);
    }

    public UserProfileResponseDto updateUserProfile(UUID userId, UserProfileUpdateDto updateDto) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        mappingService.mapToExisting(updateDto, profile);
        profile = userProfileRepository.save(profile);
        return mappingService.map(profile, UserProfileResponseDto.class);
    }

    public UserProfileResponseDto getUserProfile(UUID userId) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        return mappingService.map(profile, UserProfileResponseDto.class);
    }

    public void updateTeacherPortfolio(UUID teacherId, Map<String, Object> portfolioData) {
        UserProfile profile = userProfileRepository.findByUserId(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher profile not found"));
        
        Map<String, Object> professionalInfo = profile.getProfessionalInfo();
        professionalInfo.put("portfolio", portfolioData);
        profile.setProfessionalInfo(professionalInfo);
        
        userProfileRepository.save(profile);
    }

    // ============================================================================
    // SOCIAL MEDIA INTEGRATION
    // ============================================================================

    public void updateSocialMediaLinks(UUID userId, Map<String, String> socialLinks) {
        UserProfile profile = userProfileRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User profile not found"));
        
        profile.setLinkedinUrl(socialLinks.get("linkedin"));
        profile.setTwitterHandle(socialLinks.get("twitter"));
        profile.setWebsite(socialLinks.get("website"));
        
        userProfileRepository.save(profile);
    }

    public List<UserResponseDto> getTeachersWithSocialMedia() {
        List<User> teachers = userRepository.findTeachersWithSocialMedia();
        return mappingService.getUserMapper().toResponseDtoList(teachers);
    }

    // ============================================================================
    // SUBSCRIPTION MANAGEMENT
    // ============================================================================

    public void updateSubscriptionTier(UUID userId, String tier) {
        User user = findUserById(userId);
        Map<String, Object> metadata = user.getMetadata();
        metadata.put("subscriptionTier", tier);
        metadata.put("subscriptionUpdatedAt", LocalDateTime.now());
        user.setMetadata(metadata);
        userRepository.save(user);
    }

    public List<UserResponseDto> getUsersBySubscriptionTier(String tier) {
        List<User> users = userRepository.findBySubscriptionTier(tier);
        return mappingService.getUserMapper().toResponseDtoList(users);
    }

    // ============================================================================
    // SECURITY AND ACTIVITY
    // ============================================================================

    public void lockUser(UUID userId, String reason) {
        User user = findUserById(userId);
        user.setStatus(UserStatus.SUSPENDED);
        user.setLockedUntil(LocalDateTime.now().plusDays(30)); // 30 days lock
        
        Map<String, Object> metadata = user.getMetadata();
        metadata.put("lockReason", reason);
        metadata.put("lockedAt", LocalDateTime.now());
        user.setMetadata(metadata);
        
        userRepository.save(user);
    }

    public void unlockUser(UUID userId) {
        User user = findUserById(userId);
        user.setStatus(UserStatus.ACTIVE);
        user.setLockedUntil(null);
        userRepository.save(user);
    }

    public void updateLastActivity(UUID userId) {
        userRepository.updateLastActivity(userId, LocalDateTime.now());
    }

    public List<UserResponseDto> getLockedUsers() {
        List<User> users = userRepository.findLockedUsers();
        return mappingService.getUserMapper().toResponseDtoList(users);
    }

    public List<UserResponseDto> getUsersWithFailedAttempts(int minAttempts) {
        List<User> users = userRepository.findUsersWithFailedAttempts(minAttempts);
        return mappingService.getUserMapper().toResponseDtoList(users);
    }

    // ============================================================================
    // BULK OPERATIONS
    // ============================================================================

    public void bulkUpdateLastActivity(List<UUID> userIds) {
        LocalDateTime timestamp = LocalDateTime.now();
        for (UUID userId : userIds) {
            userRepository.updateLastActivity(userId, timestamp);
        }
    }

    public void bulkLockUsers(List<UUID> userIds, String reason) {
        for (UUID userId : userIds) {
            lockUser(userId, reason);
        }
    }

    public void bulkUnlockUsers(List<UUID> userIds) {
        for (UUID userId : userIds) {
            unlockUser(userId);
        }
    }

    public void bulkUpdateStatus(List<UUID> userIds, UserStatus status) {
        userRepository.bulkUpdateStatus(userIds, status);
    }

    // ============================================================================
    // SEARCH AND FILTERING
    // ============================================================================

    public Page<UserResponseDto> searchUsers(String searchTerm, UserRole role, UserStatus status, Pageable pageable) {
        Page<User> users = userRepository.searchUsers(searchTerm, role, status, pageable);
        return mappingService.getUserMapper().toResponseDtoPage(users);
    }

    public Page<UserResponseDto> searchTeachers(String searchTerm, String specialization, 
                                               Boolean verified, Pageable pageable) {
        Page<User> teachers = userRepository.searchTeachers(searchTerm, specialization, verified, pageable);
        return mappingService.getUserMapper().toResponseDtoPage(teachers);
    }

    public List<UserResponseDto> getRecentlyRegisteredUsers(int days) {
        LocalDateTime since = LocalDateTime.now().minusDays(days);
        List<User> users = userRepository.findRecentlyRegistered(since);
        return mappingService.getUserMapper().toResponseDtoList(users);
    }

    public List<UserResponseDto> getActiveUsersInPeriod(LocalDateTime start, LocalDateTime end) {
        List<User> users = userRepository.findActiveUsersInPeriod(start, end);
        return mappingService.getUserMapper().toResponseDtoList(users);
    }

    // ============================================================================
    // ANALYTICS SUPPORT
    // ============================================================================

    public Map<String, Long> getUserCountByRole() {
        return userRepository.countUsersByRole().stream()
                .collect(java.util.stream.Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
    }

    public Map<String, Long> getUserCountByStatus() {
        return userRepository.countUsersByStatus().stream()
                .collect(java.util.stream.Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
    }

    public Map<String, Long> getRegistrationStatsForPeriod(LocalDateTime start, LocalDateTime end) {
        return userRepository.getRegistrationStats(start, end).stream()
                .collect(java.util.stream.Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
    }

    public Map<String, Object> getTeacherActivityStats() {
        return userRepository.getTeacherActivityStats().stream()
                .collect(java.util.stream.Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> arr[1]
                ));
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private User findUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public boolean isEmailAvailable(String email) {
        return !userRepository.findByEmail(email).isPresent();
    }

    public boolean isUsernameAvailable(String username) {
        return !userRepository.findByUsername(username).isPresent();
    }

    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = findUserById(userId);
        // Verify current password
        if (!mappingService.getUserMapper().verifyPassword(currentPassword, user.getPasswordHash())) {
            throw new RuntimeException("Current password is incorrect");
        }
        
        mappingService.getUserMapper().updatePassword(user, newPassword);
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);
    }

    public void resetPassword(UUID userId, String newPassword) {
        User user = findUserById(userId);
        mappingService.getUserMapper().updatePassword(user, newPassword);
        user.setMustChangePassword(true);
        user.setLastPasswordChange(LocalDateTime.now());
        userRepository.save(user);
    }
}