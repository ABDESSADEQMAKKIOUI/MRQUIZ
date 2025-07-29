package com.example.mrquiz.repository.auth;

import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.enums.UserRole;
import com.example.mrquiz.enums.UserStatus;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends BaseRepository<User> {
    
    // ===== BASIC USER QUERIES =====
    
    /**
     * Find user by email address
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email or username
     */
    @Query("SELECT u FROM User u WHERE u.email = :identifier OR u.username = :identifier")
    Optional<User> findByEmailOrUsername(@Param("identifier") String identifier);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    // ===== TEACHER-SPECIFIC QUERIES =====
    
    /**
     * Find all active teachers for independent teacher functionality
     */
    @Query("SELECT u FROM User u WHERE u.role = :role AND u.status = 'ACTIVE'")
    Page<User> findActiveTeachers(@Param("role") UserRole role, Pageable pageable);
    
    /**
     * Find teachers by specialization (stored in metadata)
     */
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_EXTRACT(u.metadata, '$.specialization') LIKE %:specialization%")
    List<User> findTeachersBySpecialization(@Param("specialization") String specialization);
    
    /**
     * Find teachers with professional verification
     */
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_EXTRACT(u.metadata, '$.professionalVerified') = true")
    List<User> findVerifiedTeachers();
    
    /**
     * Find teachers by qualification level
     */
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_EXTRACT(u.metadata, '$.qualificationLevel') = :level")
    List<User> findTeachersByQualification(@Param("level") String level);
    
    // ===== USER STATUS AND SECURITY =====
    
    /**
     * Find users by status
     */
    List<User> findByStatus(UserStatus status);
    
    /**
     * Find users by role and status
     */
    List<User> findByRoleAndStatus(UserRole role, UserStatus status);
    
    /**
     * Find locked users (security)
     */
    @Query("SELECT u FROM User u WHERE u.lockedUntil > :now")
    List<User> findLockedUsers(@Param("now") LocalDateTime now);
    
    /**
     * Find users with failed login attempts above threshold
     */
    @Query("SELECT u FROM User u WHERE u.failedLoginAttempts >= :threshold")
    List<User> findUsersWithFailedAttempts(@Param("threshold") Integer threshold);
    
    /**
     * Find users requiring password change
     */
    List<User> findByMustChangePasswordTrue();
    
    /**
     * Find users with MFA enabled
     */
    List<User> findByMfaEnabledTrue();
    
    // ===== ACTIVITY AND ENGAGEMENT =====
    
    /**
     * Find recently active users
     */
    @Query("SELECT u FROM User u WHERE u.lastActivity > :since ORDER BY u.lastActivity DESC")
    List<User> findRecentlyActiveUsers(@Param("since") LocalDateTime since);
    
    /**
     * Find inactive users (no activity for specified period)
     */
    @Query("SELECT u FROM User u WHERE u.lastActivity < :threshold OR u.lastActivity IS NULL")
    List<User> findInactiveUsers(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Find new users registered in period
     */
    @Query("SELECT u FROM User u WHERE u.createdAt BETWEEN :start AND :end ORDER BY u.createdAt DESC")
    List<User> findNewUsersInPeriod(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    // ===== TEACHER PORTFOLIO AND CONTENT =====
    
    /**
     * Find teachers with most created quizzes
     */
    @Query("SELECT u FROM User u JOIN Quiz q ON u.id = q.creator.id " +
           "WHERE u.role = 'TEACHER' GROUP BY u.id ORDER BY COUNT(q.id) DESC")
    List<User> findMostActiveTeachers(Pageable pageable);
    
    /**
     * Find teachers by content creation activity
     */
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND (SELECT COUNT(q) FROM Quiz q WHERE q.creator.id = u.id) >= :minQuizzes")
    List<User> findTeachersWithMinimumContent(@Param("minQuizzes") Long minQuizzes);
    
    // ===== SUBSCRIPTION AND PAYMENT =====
    
    /**
     * Find teachers with premium subscriptions
     */
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_EXTRACT(u.metadata, '$.subscriptionTier') IN ('premium', 'professional')")
    List<User> findPremiumTeachers();
    
    /**
     * Find teachers eligible for revenue sharing
     */
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND JSON_EXTRACT(u.metadata, '$.revenueShareEnabled') = true")
    List<User> findRevenueShareEligibleTeachers();
    
    // ===== SOCIAL AND PROFESSIONAL NETWORKS =====
    
    /**
     * Find teachers with social media integration
     */
    @Query("SELECT u FROM User u JOIN UserProfile up ON u.id = up.user.id " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND (up.linkedinUrl IS NOT NULL OR up.twitterHandle IS NOT NULL)")
    List<User> findTeachersWithSocialMedia();
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Update last activity for user
     */
    @Modifying
    @Query("UPDATE User u SET u.lastActivity = :timestamp WHERE u.id = :userId")
    void updateLastActivity(@Param("userId") UUID userId, @Param("timestamp") LocalDateTime timestamp);
    
    /**
     * Update failed login attempts
     */
    @Modifying
    @Query("UPDATE User u SET u.failedLoginAttempts = :attempts WHERE u.id = :userId")
    void updateFailedLoginAttempts(@Param("userId") UUID userId, @Param("attempts") Integer attempts);
    
    /**
     * Lock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = :lockUntil, u.failedLoginAttempts = :attempts WHERE u.id = :userId")
    void lockUserAccount(@Param("userId") UUID userId, @Param("lockUntil") LocalDateTime lockUntil, 
                        @Param("attempts") Integer attempts);
    
    /**
     * Unlock user account
     */
    @Modifying
    @Query("UPDATE User u SET u.lockedUntil = NULL, u.failedLoginAttempts = 0 WHERE u.id = :userId")
    void unlockUserAccount(@Param("userId") UUID userId);
    
    /**
     * Update user verification status
     */
    @Modifying
    @Query("UPDATE User u SET u.emailVerified = :verified WHERE u.id = :userId")
    void updateEmailVerificationStatus(@Param("userId") UUID userId, @Param("verified") Boolean verified);
    
    /**
     * Bulk update user status
     */
    @Modifying
    @Query("UPDATE User u SET u.status = :status WHERE u.id IN :userIds")
    void bulkUpdateStatus(@Param("userIds") List<UUID> userIds, @Param("status") UserStatus status);
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search users by name or email
     */
    @Query("SELECT u FROM User u WHERE " +
           "LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<User> searchUsers(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    /**
     * Search teachers with filters
     */
    @Query("SELECT u FROM User u WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "AND (:name IS NULL OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :name, '%'))) " +
           "AND (:email IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :email, '%'))) " +
           "AND (:verified IS NULL OR " +
           "    (CASE WHEN :verified = true THEN JSON_EXTRACT(u.metadata, '$.professionalVerified') = true " +
           "          ELSE JSON_EXTRACT(u.metadata, '$.professionalVerified') != true END))")
    Page<User> searchTeachersWithFilters(@Param("name") String name, 
                                       @Param("email") String email,
                                       @Param("verified") Boolean verified, 
                                       Pageable pageable);
    
    // ===== ANALYTICS SUPPORT =====
    
    /**
     * Count users by role
     */
    @Query("SELECT u.role, COUNT(u) FROM User u GROUP BY u.role")
    List<Object[]> countUsersByRole();
    
    /**
     * Count users by status
     */
    @Query("SELECT u.status, COUNT(u) FROM User u GROUP BY u.status")
    List<Object[]> countUsersByStatus();
    
    /**
     * Get user registration statistics
     */
    @Query("SELECT DATE(u.createdAt) as date, COUNT(u) as count FROM User u " +
           "WHERE u.createdAt BETWEEN :start AND :end GROUP BY DATE(u.createdAt) ORDER BY date")
    List<Object[]> getUserRegistrationStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * Get teacher activity statistics
     */
    @Query("SELECT u.id, u.firstName, u.lastName, u.email, " +
           "COUNT(DISTINCT q.id) as quizCount, " +
           "COUNT(DISTINCT qa.id) as attemptCount, " +
           "AVG(qa.percentage) as avgScore " +
           "FROM User u " +
           "LEFT JOIN Quiz q ON u.id = q.creator.id " +
           "LEFT JOIN QuizAttempt qa ON q.id = qa.quiz.id " +
           "WHERE u.role = 'TEACHER' AND u.status = 'ACTIVE' " +
           "GROUP BY u.id, u.firstName, u.lastName, u.email")
    List<Object[]> getTeacherActivityStats();
}