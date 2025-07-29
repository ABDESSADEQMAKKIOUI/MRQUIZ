package com.example.mrquiz.repository.auth;

import com.example.mrquiz.entity.auth.AuthToken;
import com.example.mrquiz.enums.TokenType;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AuthTokenRepository extends BaseRepository<AuthToken> {
    
    // ===== BASIC TOKEN QUERIES =====
    
    /**
     * Find token by hash
     */
    Optional<AuthToken> findByTokenHash(String tokenHash);
    
    /**
     * Find valid token by hash (not expired and not used)
     */
    @Query("SELECT t FROM AuthToken t WHERE t.tokenHash = :tokenHash " +
           "AND t.expiresAt > :now AND t.usedAt IS NULL")
    Optional<AuthToken> findValidToken(@Param("tokenHash") String tokenHash, @Param("now") LocalDateTime now);
    
    /**
     * Find tokens by user and type
     */
    List<AuthToken> findByUserIdAndTokenType(UUID userId, TokenType tokenType);
    
    /**
     * Find valid tokens by user and type
     */
    @Query("SELECT t FROM AuthToken t WHERE t.user.id = :userId AND t.tokenType = :tokenType " +
           "AND t.expiresAt > :now AND t.usedAt IS NULL")
    List<AuthToken> findValidTokensByUserAndType(@Param("userId") UUID userId, 
                                                @Param("tokenType") TokenType tokenType,
                                                @Param("now") LocalDateTime now);
    
    // ===== TOKEN LIFECYCLE MANAGEMENT =====
    
    /**
     * Find expired tokens
     */
    @Query("SELECT t FROM AuthToken t WHERE t.expiresAt <= :now")
    List<AuthToken> findExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Find used tokens
     */
    List<AuthToken> findByUsedAtIsNotNull();
    
    /**
     * Find tokens expiring soon
     */
    @Query("SELECT t FROM AuthToken t WHERE t.expiresAt BETWEEN :now AND :threshold AND t.usedAt IS NULL")
    List<AuthToken> findTokensExpiringSoon(@Param("now") LocalDateTime now, @Param("threshold") LocalDateTime threshold);
    
    // ===== SECURITY AND CLEANUP =====
    
    /**
     * Mark token as used
     */
    @Modifying
    @Query("UPDATE AuthToken t SET t.usedAt = :usedAt WHERE t.id = :tokenId")
    void markTokenAsUsed(@Param("tokenId") UUID tokenId, @Param("usedAt") LocalDateTime usedAt);
    
    /**
     * Delete expired tokens
     */
    @Modifying
    @Query("DELETE FROM AuthToken t WHERE t.expiresAt <= :now")
    void deleteExpiredTokens(@Param("now") LocalDateTime now);
    
    /**
     * Delete used tokens older than threshold
     */
    @Modifying
    @Query("DELETE FROM AuthToken t WHERE t.usedAt IS NOT NULL AND t.usedAt <= :threshold")
    void deleteOldUsedTokens(@Param("threshold") LocalDateTime threshold);
    
    /**
     * Revoke all tokens for user and type
     */
    @Modifying
    @Query("UPDATE AuthToken t SET t.usedAt = :revokedAt WHERE t.user.id = :userId " +
           "AND t.tokenType = :tokenType AND t.usedAt IS NULL")
    void revokeUserTokensByType(@Param("userId") UUID userId, 
                               @Param("tokenType") TokenType tokenType,
                               @Param("revokedAt") LocalDateTime revokedAt);
    
    /**
     * Revoke all tokens for user
     */
    @Modifying
    @Query("UPDATE AuthToken t SET t.usedAt = :revokedAt WHERE t.user.id = :userId AND t.usedAt IS NULL")
    void revokeAllUserTokens(@Param("userId") UUID userId, @Param("revokedAt") LocalDateTime revokedAt);
    
    // ===== EMAIL VERIFICATION SPECIFIC =====
    
    /**
     * Find latest email verification token for user
     */
    @Query("SELECT t FROM AuthToken t WHERE t.user.id = :userId AND t.tokenType = 'EMAIL_VERIFICATION' " +
           "AND t.usedAt IS NULL ORDER BY t.createdAt DESC")
    Optional<AuthToken> findLatestEmailVerificationToken(@Param("userId") UUID userId);
    
    /**
     * Count pending email verification tokens for user
     */
    @Query("SELECT COUNT(t) FROM AuthToken t WHERE t.user.id = :userId " +
           "AND t.tokenType = 'EMAIL_VERIFICATION' AND t.expiresAt > :now AND t.usedAt IS NULL")
    Long countPendingEmailVerificationTokens(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    // ===== PASSWORD RESET SPECIFIC =====
    
    /**
     * Find latest password reset token for user
     */
    @Query("SELECT t FROM AuthToken t WHERE t.user.id = :userId AND t.tokenType = 'PASSWORD_RESET' " +
           "AND t.usedAt IS NULL ORDER BY t.createdAt DESC")
    Optional<AuthToken> findLatestPasswordResetToken(@Param("userId") UUID userId);
    
    /**
     * Count recent password reset attempts
     */
    @Query("SELECT COUNT(t) FROM AuthToken t WHERE t.user.id = :userId " +
           "AND t.tokenType = 'PASSWORD_RESET' AND t.createdAt > :since")
    Long countRecentPasswordResetAttempts(@Param("userId") UUID userId, @Param("since") LocalDateTime since);
    
    // ===== MFA TOKEN SPECIFIC =====
    
    /**
     * Find valid MFA token for user
     */
    @Query("SELECT t FROM AuthToken t WHERE t.user.id = :userId AND t.tokenType = 'MFA_TOKEN' " +
           "AND t.expiresAt > :now AND t.usedAt IS NULL ORDER BY t.createdAt DESC")
    Optional<AuthToken> findValidMfaToken(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    // ===== API TOKEN MANAGEMENT =====
    
    /**
     * Find active API tokens for user
     */
    @Query("SELECT t FROM AuthToken t WHERE t.user.id = :userId AND t.tokenType = 'API_TOKEN' " +
           "AND (t.expiresAt IS NULL OR t.expiresAt > :now) AND t.usedAt IS NULL")
    List<AuthToken> findActiveApiTokens(@Param("userId") UUID userId, @Param("now") LocalDateTime now);
    
    // ===== ANALYTICS AND MONITORING =====
    
    /**
     * Get token usage statistics by type
     */
    @Query("SELECT t.tokenType, COUNT(t) as total, " +
           "COUNT(CASE WHEN t.usedAt IS NOT NULL THEN 1 END) as used, " +
           "COUNT(CASE WHEN t.expiresAt <= :now THEN 1 END) as expired " +
           "FROM AuthToken t GROUP BY t.tokenType")
    List<Object[]> getTokenStatsByType(@Param("now") LocalDateTime now);
    
    /**
     * Get token creation statistics for period
     */
    @Query("SELECT DATE(t.createdAt) as date, t.tokenType, COUNT(t) as count " +
           "FROM AuthToken t WHERE t.createdAt BETWEEN :start AND :end " +
           "GROUP BY DATE(t.createdAt), t.tokenType ORDER BY date, t.tokenType")
    List<Object[]> getTokenCreationStats(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);
    
    /**
     * Count tokens by user in period
     */
    @Query("SELECT COUNT(t) FROM AuthToken t WHERE t.user.id = :userId " +
           "AND t.createdAt BETWEEN :start AND :end")
    Long countUserTokensInPeriod(@Param("userId") UUID userId, 
                                @Param("start") LocalDateTime start, 
                                @Param("end") LocalDateTime end);
}