package com.example.mrquiz.service.auth;

import com.example.mrquiz.dto.auth.AuthTokenCreateDto;
import com.example.mrquiz.dto.auth.AuthTokenResponseDto;
import com.example.mrquiz.entity.auth.AuthToken;
import com.example.mrquiz.enums.TokenType;
import com.example.mrquiz.repository.auth.AuthTokenRepository;
import com.example.mrquiz.service.MappingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class AuthTokenService {

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom = new SecureRandom();

    // ============================================================================
    // TOKEN CREATION
    // ============================================================================

    public AuthTokenResponseDto createToken(AuthTokenCreateDto createDto) {
        String token = generateSecureToken();
        String tokenHash = passwordEncoder.encode(token);
        
        AuthToken authToken = mappingService.map(createDto, AuthToken.class);
        authToken.setTokenHash(tokenHash);
        authToken = authTokenRepository.save(authToken);
        
        AuthTokenResponseDto response = mappingService.map(authToken, AuthTokenResponseDto.class);
        response.setTokenHash(token); // Return plain token only once
        return response;
    }

    public String createEmailVerificationToken(UUID userId) {
        // Invalidate existing email verification tokens
        authTokenRepository.markTokensAsUsed(userId, TokenType.EMAIL_VERIFICATION);
        
        AuthTokenCreateDto createDto = new AuthTokenCreateDto();
        createDto.setUserId(userId);
        createDto.setTokenType(TokenType.EMAIL_VERIFICATION);
        createDto.setExpiresAt(LocalDateTime.now().plusHours(24)); // 24 hours expiry
        
        AuthTokenResponseDto response = createToken(createDto);
        return response.getTokenHash(); // This contains the plain token
    }

    public String createPasswordResetToken(UUID userId) {
        // Invalidate existing password reset tokens
        authTokenRepository.markTokensAsUsed(userId, TokenType.PASSWORD_RESET);
        
        AuthTokenCreateDto createDto = new AuthTokenCreateDto();
        createDto.setUserId(userId);
        createDto.setTokenType(TokenType.PASSWORD_RESET);
        createDto.setExpiresAt(LocalDateTime.now().plusHours(1)); // 1 hour expiry
        
        AuthTokenResponseDto response = createToken(createDto);
        return response.getTokenHash(); // This contains the plain token
    }

    public String createMfaToken(UUID userId) {
        AuthTokenCreateDto createDto = new AuthTokenCreateDto();
        createDto.setUserId(userId);
        createDto.setTokenType(TokenType.MFA_TOKEN);
        createDto.setExpiresAt(LocalDateTime.now().plusMinutes(5)); // 5 minutes expiry
        
        AuthTokenResponseDto response = createToken(createDto);
        return response.getTokenHash(); // This contains the plain token
    }

    public String createApiToken(UUID userId, Map<String, Object> metadata) {
        AuthTokenCreateDto createDto = new AuthTokenCreateDto();
        createDto.setUserId(userId);
        createDto.setTokenType(TokenType.API_TOKEN);
        createDto.setExpiresAt(LocalDateTime.now().plusYears(1)); // 1 year expiry
        createDto.setMetadata(metadata);
        
        AuthTokenResponseDto response = createToken(createDto);
        return response.getTokenHash(); // This contains the plain token
    }

    public String createRefreshToken(UUID userId) {
        AuthTokenCreateDto createDto = new AuthTokenCreateDto();
        createDto.setUserId(userId);
        createDto.setTokenType(TokenType.REFRESH_TOKEN);
        createDto.setExpiresAt(LocalDateTime.now().plusDays(30)); // 30 days expiry
        
        AuthTokenResponseDto response = createToken(createDto);
        return response.getTokenHash(); // This contains the plain token
    }

    // ============================================================================
    // TOKEN VALIDATION
    // ============================================================================

    public Optional<AuthTokenResponseDto> validateToken(String token, TokenType tokenType) {
        List<AuthToken> tokens = authTokenRepository.findValidTokensByType(tokenType);
        
        for (AuthToken authToken : tokens) {
            if (passwordEncoder.matches(token, authToken.getTokenHash())) {
                return Optional.of(mappingService.map(authToken, AuthTokenResponseDto.class));
            }
        }
        
        return Optional.empty();
    }

    public boolean verifyEmailToken(String token, UUID userId) {
        Optional<AuthTokenResponseDto> tokenOpt = validateToken(token, TokenType.EMAIL_VERIFICATION);
        if (tokenOpt.isPresent() && tokenOpt.get().getUserId().equals(userId)) {
            markTokenAsUsed(tokenOpt.get().getId());
            return true;
        }
        return false;
    }

    public boolean verifyPasswordResetToken(String token, UUID userId) {
        Optional<AuthTokenResponseDto> tokenOpt = validateToken(token, TokenType.PASSWORD_RESET);
        if (tokenOpt.isPresent() && tokenOpt.get().getUserId().equals(userId)) {
            markTokenAsUsed(tokenOpt.get().getId());
            return true;
        }
        return false;
    }

    public boolean verifyMfaToken(String token, UUID userId) {
        Optional<AuthTokenResponseDto> tokenOpt = validateToken(token, TokenType.MFA_TOKEN);
        if (tokenOpt.isPresent() && tokenOpt.get().getUserId().equals(userId)) {
            markTokenAsUsed(tokenOpt.get().getId());
            return true;
        }
        return false;
    }

    public Optional<UUID> validateApiToken(String token) {
        Optional<AuthTokenResponseDto> tokenOpt = validateToken(token, TokenType.API_TOKEN);
        return tokenOpt.map(AuthTokenResponseDto::getUserId);
    }

    // ============================================================================
    // TOKEN MANAGEMENT
    // ============================================================================

    public void markTokenAsUsed(UUID tokenId) {
        authTokenRepository.markTokenAsUsed(tokenId, LocalDateTime.now());
    }

    public void revokeUserTokens(UUID userId, TokenType tokenType) {
        authTokenRepository.markTokensAsUsed(userId, tokenType);
    }

    public void revokeAllUserTokens(UUID userId) {
        authTokenRepository.revokeAllUserTokens(userId);
    }

    public void deleteExpiredTokens() {
        authTokenRepository.deleteExpiredTokens(LocalDateTime.now());
    }

    public void deleteOldUsedTokens(int daysOld) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        authTokenRepository.deleteOldUsedTokens(cutoff);
    }

    // ============================================================================
    // TOKEN QUERIES
    // ============================================================================

    public List<AuthTokenResponseDto> getUserTokens(UUID userId) {
        List<AuthToken> tokens = authTokenRepository.findByUserId(userId);
        return mappingService.mapList(tokens, AuthTokenResponseDto.class);
    }

    public List<AuthTokenResponseDto> getValidTokensByUser(UUID userId) {
        List<AuthToken> tokens = authTokenRepository.findValidTokensByUser(userId);
        return mappingService.mapList(tokens, AuthTokenResponseDto.class);
    }

    public List<AuthTokenResponseDto> getTokensByType(TokenType tokenType) {
        List<AuthToken> tokens = authTokenRepository.findByTokenType(tokenType);
        return mappingService.mapList(tokens, AuthTokenResponseDto.class);
    }

    public List<AuthTokenResponseDto> getExpiringTokens(int hoursFromNow) {
        LocalDateTime expiryThreshold = LocalDateTime.now().plusHours(hoursFromNow);
        List<AuthToken> tokens = authTokenRepository.findExpiringTokens(expiryThreshold);
        return mappingService.mapList(tokens, AuthTokenResponseDto.class);
    }

    // ============================================================================
    // ANALYTICS AND MONITORING
    // ============================================================================

    public Map<String, Long> getTokenUsageStats() {
        return authTokenRepository.getTokenUsageStatsByType().stream()
                .collect(java.util.stream.Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
    }

    public Map<String, Long> getTokenCreationStats(LocalDateTime start, LocalDateTime end) {
        return authTokenRepository.getTokenCreationStats(start, end).stream()
                .collect(java.util.stream.Collectors.toMap(
                    arr -> arr[0].toString(),
                    arr -> (Long) arr[1]
                ));
    }

    public long countActiveTokensByType(TokenType tokenType) {
        return authTokenRepository.countActiveTokensByType(tokenType);
    }

    public long countExpiredTokens() {
        return authTokenRepository.countExpiredTokens(LocalDateTime.now());
    }

    // ============================================================================
    // CLEANUP OPERATIONS
    // ============================================================================

    public void performTokenCleanup() {
        // Delete expired tokens
        deleteExpiredTokens();
        
        // Delete old used tokens (older than 30 days)
        deleteOldUsedTokens(30);
    }

    public void performSecurityCleanup(UUID userId) {
        // Revoke all existing tokens for security
        revokeAllUserTokens(userId);
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private String generateSecureToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return bytesToHex(tokenBytes);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            result.append(String.format("%02x", b));
        }
        return result.toString();
    }

    // Generate numeric OTP for MFA
    public String generateNumericOtp(int length) {
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < length; i++) {
            otp.append(secureRandom.nextInt(10));
        }
        return otp.toString();
    }

    // Generate alphanumeric code
    public String generateAlphanumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return code.toString();
    }
}