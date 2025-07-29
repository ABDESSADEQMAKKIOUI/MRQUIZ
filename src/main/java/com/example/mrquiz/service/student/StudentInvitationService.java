package com.example.mrquiz.service.student;

import com.example.mrquiz.service.MappingService;
import com.example.mrquiz.service.notification.EmailService;
import com.example.mrquiz.service.quiz.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StudentInvitationService {

    @Autowired
    private MappingService mappingService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private QuizService quizService;

    private final SecureRandom secureRandom = new SecureRandom();

    // ============================================================================
    // EMAIL INVITATION SYSTEM
    // ============================================================================

    public void sendBulkEmailInvitations(UUID quizId, List<String> emailAddresses, 
                                        Map<String, Object> customization) {
        for (String email : emailAddresses) {
            sendEmailInvitation(quizId, email, customization);
        }
    }

    public void sendEmailInvitation(UUID quizId, String email, Map<String, Object> customization) {
        String invitationToken = generateInvitationToken();
        
        Map<String, Object> emailData = new HashMap<>();
        emailData.put("quizId", quizId);
        emailData.put("email", email);
        emailData.put("invitationToken", invitationToken);
        emailData.put("sentAt", LocalDateTime.now());
        emailData.put("customization", customization);
        
        // Store invitation data for tracking
        storeInvitationData(quizId, email, emailData);
        
        // Send email
        emailService.sendQuizInvitation(email, quizId, invitationToken, customization);
    }

    public void sendPersonalizedInvitations(UUID quizId, Map<String, Map<String, Object>> invitations) {
        invitations.forEach((email, personalization) -> {
            sendEmailInvitation(quizId, email, personalization);
        });
    }

    public void setupAutomatedReminders(UUID quizId, List<String> emails, 
                                       List<Integer> reminderHours) {
        Map<String, Object> reminderConfig = new HashMap<>();
        reminderConfig.put("quizId", quizId);
        reminderConfig.put("emails", emails);
        reminderConfig.put("reminderHours", reminderHours);
        reminderConfig.put("setupAt", LocalDateTime.now());
        
        // Schedule reminders (would integrate with scheduling system)
        scheduleReminders(reminderConfig);
    }

    public Map<String, Object> getEmailEngagementAnalytics(UUID quizId) {
        Map<String, Object> analytics = new HashMap<>();
        
        // Get invitation data from storage
        List<Map<String, Object>> invitations = getInvitationData(quizId);
        
        long totalSent = invitations.size();
        long opened = invitations.stream()
                .mapToLong(inv -> (Boolean) inv.getOrDefault("opened", false) ? 1 : 0)
                .sum();
        long clicked = invitations.stream()
                .mapToLong(inv -> (Boolean) inv.getOrDefault("clicked", false) ? 1 : 0)
                .sum();
        long participated = invitations.stream()
                .mapToLong(inv -> (Boolean) inv.getOrDefault("participated", false) ? 1 : 0)
                .sum();
        
        analytics.put("totalSent", totalSent);
        analytics.put("openRate", totalSent > 0 ? (double) opened / totalSent : 0);
        analytics.put("clickRate", totalSent > 0 ? (double) clicked / totalSent : 0);
        analytics.put("participationRate", totalSent > 0 ? (double) participated / totalSent : 0);
        
        return analytics;
    }

    // ============================================================================
    // JOIN CODE SYSTEM
    // ============================================================================

    public String generateJoinCode(UUID quizId, Map<String, Object> codeSettings) {
        String code = generateAlphanumericCode(8);
        
        Map<String, Object> joinCodeData = new HashMap<>();
        joinCodeData.put("code", code);
        joinCodeData.put("quizId", quizId);
        joinCodeData.put("createdAt", LocalDateTime.now());
        joinCodeData.put("settings", codeSettings);
        
        // Set expiration if specified
        if (codeSettings.containsKey("expirationHours")) {
            int hours = (Integer) codeSettings.get("expirationHours");
            joinCodeData.put("expiresAt", LocalDateTime.now().plusHours(hours));
        }
        
        // Store join code data
        storeJoinCodeData(code, joinCodeData);
        
        return code;
    }

    public List<String> generateBulkJoinCodes(UUID quizId, int count, Map<String, Object> settings) {
        List<String> codes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String code = generateJoinCode(quizId, settings);
            codes.add(code);
        }
        return codes;
    }

    public boolean validateJoinCode(String code) {
        Map<String, Object> codeData = getJoinCodeData(code);
        if (codeData == null) {
            return false;
        }
        
        // Check expiration
        LocalDateTime expiresAt = (LocalDateTime) codeData.get("expiresAt");
        if (expiresAt != null && LocalDateTime.now().isAfter(expiresAt)) {
            return false;
        }
        
        return true;
    }

    public Map<String, Object> getJoinCodeAnalytics(String code) {
        Map<String, Object> codeData = getJoinCodeData(code);
        if (codeData == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("code", code);
        analytics.put("createdAt", codeData.get("createdAt"));
        analytics.put("usageCount", codeData.getOrDefault("usageCount", 0));
        analytics.put("lastUsed", codeData.get("lastUsed"));
        analytics.put("isActive", validateJoinCode(code));
        
        return analytics;
    }

    public void trackJoinCodeUsage(String code, UUID userId) {
        Map<String, Object> codeData = getJoinCodeData(code);
        if (codeData != null) {
            int usageCount = (Integer) codeData.getOrDefault("usageCount", 0);
            codeData.put("usageCount", usageCount + 1);
            codeData.put("lastUsed", LocalDateTime.now());
            
            @SuppressWarnings("unchecked")
            List<UUID> users = (List<UUID>) codeData.getOrDefault("usedBy", new ArrayList<>());
            if (!users.contains(userId)) {
                users.add(userId);
                codeData.put("usedBy", users);
            }
            
            updateJoinCodeData(code, codeData);
        }
    }

    // ============================================================================
    // QR CODE INTEGRATION
    // ============================================================================

    public Map<String, Object> generateQRCode(UUID quizId, Map<String, Object> styling) {
        String qrData = generateQRCodeData(quizId);
        
        Map<String, Object> qrCodeInfo = new HashMap<>();
        qrCodeInfo.put("data", qrData);
        qrCodeInfo.put("quizId", quizId);
        qrCodeInfo.put("styling", styling);
        qrCodeInfo.put("createdAt", LocalDateTime.now());
        qrCodeInfo.put("scanCount", 0);
        
        // Generate QR code image (would integrate with QR code library)
        String qrCodeUrl = generateQRCodeImage(qrData, styling);
        qrCodeInfo.put("imageUrl", qrCodeUrl);
        
        // Store QR code data
        storeQRCodeData(qrData, qrCodeInfo);
        
        return qrCodeInfo;
    }

    public List<Map<String, Object>> generateBatchQRCodes(UUID quizId, int count, 
                                                          Map<String, Object> styling) {
        List<Map<String, Object>> qrCodes = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Map<String, Object> qrCode = generateQRCode(quizId, styling);
            qrCode.put("batchNumber", i + 1);
            qrCodes.add(qrCode);
        }
        return qrCodes;
    }

    public Map<String, Object> getQRCodeAnalytics(String qrData) {
        Map<String, Object> qrCodeInfo = getQRCodeData(qrData);
        if (qrCodeInfo == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("scanCount", qrCodeInfo.getOrDefault("scanCount", 0));
        analytics.put("createdAt", qrCodeInfo.get("createdAt"));
        analytics.put("lastScanned", qrCodeInfo.get("lastScanned"));
        analytics.put("uniqueScans", qrCodeInfo.getOrDefault("uniqueUsers", new HashSet<>()).size());
        
        return analytics;
    }

    public void trackQRCodeScan(String qrData, UUID userId) {
        Map<String, Object> qrCodeInfo = getQRCodeData(qrData);
        if (qrCodeInfo != null) {
            int scanCount = (Integer) qrCodeInfo.getOrDefault("scanCount", 0);
            qrCodeInfo.put("scanCount", scanCount + 1);
            qrCodeInfo.put("lastScanned", LocalDateTime.now());
            
            @SuppressWarnings("unchecked")
            Set<UUID> uniqueUsers = (Set<UUID>) qrCodeInfo.getOrDefault("uniqueUsers", new HashSet<>());
            uniqueUsers.add(userId);
            qrCodeInfo.put("uniqueUsers", uniqueUsers);
            
            updateQRCodeData(qrData, qrCodeInfo);
        }
    }

    // ============================================================================
    // DIRECT LINK SHARING
    // ============================================================================

    public String generateDirectLink(UUID quizId, Map<String, Object> parameters) {
        String linkId = generateSecureToken();
        String baseUrl = "https://app.mrquiz.com/quiz/";
        
        StringBuilder linkBuilder = new StringBuilder(baseUrl);
        linkBuilder.append(quizId);
        linkBuilder.append("?ref=").append(linkId);
        
        // Add parameters
        parameters.forEach((key, value) -> 
            linkBuilder.append("&").append(key).append("=").append(value));
        
        String fullLink = linkBuilder.toString();
        
        // Store link data for tracking
        Map<String, Object> linkData = new HashMap<>();
        linkData.put("linkId", linkId);
        linkData.put("quizId", quizId);
        linkData.put("fullLink", fullLink);
        linkData.put("parameters", parameters);
        linkData.put("createdAt", LocalDateTime.now());
        linkData.put("clickCount", 0);
        
        storeLinkData(linkId, linkData);
        
        return fullLink;
    }

    public String generateShortenedUrl(String originalUrl) {
        String shortCode = generateAlphanumericCode(6);
        String shortUrl = "https://mrqz.co/" + shortCode;
        
        Map<String, Object> urlData = new HashMap<>();
        urlData.put("shortCode", shortCode);
        urlData.put("originalUrl", originalUrl);
        urlData.put("shortUrl", shortUrl);
        urlData.put("createdAt", LocalDateTime.now());
        urlData.put("clickCount", 0);
        
        storeUrlData(shortCode, urlData);
        
        return shortUrl;
    }

    public Map<String, Object> getLinkAnalytics(String linkId) {
        Map<String, Object> linkData = getLinkData(linkId);
        if (linkData == null) {
            return new HashMap<>();
        }
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("clickCount", linkData.getOrDefault("clickCount", 0));
        analytics.put("createdAt", linkData.get("createdAt"));
        analytics.put("lastClicked", linkData.get("lastClicked"));
        analytics.put("uniqueClicks", linkData.getOrDefault("uniqueUsers", new HashSet<>()).size());
        
        return analytics;
    }

    public void trackLinkClick(String linkId, UUID userId, Map<String, Object> clickData) {
        Map<String, Object> linkData = getLinkData(linkId);
        if (linkData != null) {
            int clickCount = (Integer) linkData.getOrDefault("clickCount", 0);
            linkData.put("clickCount", clickCount + 1);
            linkData.put("lastClicked", LocalDateTime.now());
            
            @SuppressWarnings("unchecked")
            Set<UUID> uniqueUsers = (Set<UUID>) linkData.getOrDefault("uniqueUsers", new HashSet<>());
            uniqueUsers.add(userId);
            linkData.put("uniqueUsers", uniqueUsers);
            
            // Store click details
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> clicks = (List<Map<String, Object>>) linkData.getOrDefault("clicks", new ArrayList<>());
            Map<String, Object> click = new HashMap<>(clickData);
            click.put("userId", userId);
            click.put("timestamp", LocalDateTime.now());
            clicks.add(click);
            linkData.put("clicks", clicks);
            
            updateLinkData(linkId, linkData);
        }
    }

    // ============================================================================
    // FLEXIBLE STUDENT ACCESS
    // ============================================================================

    public UUID createGuestSession(UUID quizId, Map<String, Object> guestData) {
        UUID guestSessionId = UUID.randomUUID();
        
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("sessionId", guestSessionId);
        sessionData.put("quizId", quizId);
        sessionData.put("guestData", guestData);
        sessionData.put("createdAt", LocalDateTime.now());
        sessionData.put("isGuest", true);
        sessionData.put("temporary", true);
        
        // Set session expiration (24 hours)
        sessionData.put("expiresAt", LocalDateTime.now().plusHours(24));
        
        storeGuestSession(guestSessionId, sessionData);
        
        return guestSessionId;
    }

    public boolean validateGuestSession(UUID sessionId) {
        Map<String, Object> sessionData = getGuestSession(sessionId);
        if (sessionData == null) {
            return false;
        }
        
        LocalDateTime expiresAt = (LocalDateTime) sessionData.get("expiresAt");
        return expiresAt == null || LocalDateTime.now().isBefore(expiresAt);
    }

    public void encourageRegistration(UUID guestSessionId, Map<String, Object> incentives) {
        Map<String, Object> sessionData = getGuestSession(guestSessionId);
        if (sessionData != null) {
            sessionData.put("registrationIncentives", incentives);
            sessionData.put("registrationEncouraged", true);
            sessionData.put("encouragedAt", LocalDateTime.now());
            
            updateGuestSession(guestSessionId, sessionData);
        }
    }

    public Map<String, Object> getConversionTracking(UUID quizId) {
        List<Map<String, Object>> guestSessions = getGuestSessionsByQuiz(quizId);
        
        long totalGuests = guestSessions.size();
        long converted = guestSessions.stream()
                .mapToLong(session -> (Boolean) session.getOrDefault("converted", false) ? 1 : 0)
                .sum();
        long encouraged = guestSessions.stream()
                .mapToLong(session -> (Boolean) session.getOrDefault("registrationEncouraged", false) ? 1 : 0)
                .sum();
        
        Map<String, Object> tracking = new HashMap<>();
        tracking.put("totalGuests", totalGuests);
        tracking.put("conversions", converted);
        tracking.put("conversionRate", totalGuests > 0 ? (double) converted / totalGuests : 0);
        tracking.put("encouraged", encouraged);
        tracking.put("encouragementRate", totalGuests > 0 ? (double) encouraged / totalGuests : 0);
        
        return tracking;
    }

    // ============================================================================
    // PRIVACY CONTROLS
    // ============================================================================

    public void setPrivacySettings(UUID quizId, Map<String, Object> privacySettings) {
        Map<String, Object> settings = new HashMap<>();
        settings.put("quizId", quizId);
        settings.put("privacySettings", privacySettings);
        settings.put("updatedAt", LocalDateTime.now());
        
        storePrivacySettings(quizId, settings);
    }

    public Map<String, Object> getPrivacySettings(UUID quizId) {
        Map<String, Object> settings = getStoredPrivacySettings(quizId);
        return settings != null ? 
            (Map<String, Object>) settings.get("privacySettings") : 
            getDefaultPrivacySettings();
    }

    private Map<String, Object> getDefaultPrivacySettings() {
        Map<String, Object> defaults = new HashMap<>();
        defaults.put("allowGuestAccess", false);
        defaults.put("requireEmail", true);
        defaults.put("collectAnalytics", true);
        defaults.put("shareResults", false);
        defaults.put("dataRetentionDays", 365);
        return defaults;
    }

    // ============================================================================
    // HELPER METHODS
    // ============================================================================

    private String generateInvitationToken() {
        return generateSecureToken();
    }

    private String generateAlphanumericCode(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            code.append(chars.charAt(secureRandom.nextInt(chars.length())));
        }
        return code.toString();
    }

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

    private String generateQRCodeData(UUID quizId) {
        return "quiz:" + quizId + ":" + generateSecureToken().substring(0, 8);
    }

    private String generateQRCodeImage(String data, Map<String, Object> styling) {
        // This would integrate with a QR code generation library
        return "https://api.qrserver.com/v1/create-qr-code/?data=" + data;
    }

    // ============================================================================
    // DATA STORAGE METHODS (would be implemented with actual storage)
    // ============================================================================

    private void storeInvitationData(UUID quizId, String email, Map<String, Object> data) {
        // Implementation would store in database or cache
    }

    private List<Map<String, Object>> getInvitationData(UUID quizId) {
        // Implementation would retrieve from database or cache
        return new ArrayList<>();
    }

    private void storeJoinCodeData(String code, Map<String, Object> data) {
        // Implementation would store in database or cache
    }

    private Map<String, Object> getJoinCodeData(String code) {
        // Implementation would retrieve from database or cache
        return new HashMap<>();
    }

    private void updateJoinCodeData(String code, Map<String, Object> data) {
        // Implementation would update in database or cache
    }

    private void storeQRCodeData(String qrData, Map<String, Object> data) {
        // Implementation would store in database or cache
    }

    private Map<String, Object> getQRCodeData(String qrData) {
        // Implementation would retrieve from database or cache
        return new HashMap<>();
    }

    private void updateQRCodeData(String qrData, Map<String, Object> data) {
        // Implementation would update in database or cache
    }

    private void storeLinkData(String linkId, Map<String, Object> data) {
        // Implementation would store in database or cache
    }

    private Map<String, Object> getLinkData(String linkId) {
        // Implementation would retrieve from database or cache
        return new HashMap<>();
    }

    private void updateLinkData(String linkId, Map<String, Object> data) {
        // Implementation would update in database or cache
    }

    private void storeUrlData(String shortCode, Map<String, Object> data) {
        // Implementation would store in database or cache
    }

    private void storeGuestSession(UUID sessionId, Map<String, Object> data) {
        // Implementation would store in database or cache
    }

    private Map<String, Object> getGuestSession(UUID sessionId) {
        // Implementation would retrieve from database or cache
        return new HashMap<>();
    }

    private void updateGuestSession(UUID sessionId, Map<String, Object> data) {
        // Implementation would update in database or cache
    }

    private List<Map<String, Object>> getGuestSessionsByQuiz(UUID quizId) {
        // Implementation would retrieve from database or cache
        return new ArrayList<>();
    }

    private void storePrivacySettings(UUID quizId, Map<String, Object> settings) {
        // Implementation would store in database or cache
    }

    private Map<String, Object> getStoredPrivacySettings(UUID quizId) {
        // Implementation would retrieve from database or cache
        return null;
    }

    private void scheduleReminders(Map<String, Object> reminderConfig) {
        // Implementation would integrate with scheduling system
    }
}