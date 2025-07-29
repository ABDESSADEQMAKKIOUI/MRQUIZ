package com.example.mrquiz.repository.security;

import com.example.mrquiz.entity.security.AuditLog;
import com.example.mrquiz.enums.SeverityLevel;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AuditLogRepository extends BaseRepository<AuditLog> {
    
    // ===== BASIC AUDIT QUERIES =====
    
    /**
     * Find audit logs by user
     */
    Page<AuditLog> findByUserId(UUID userId, Pageable pageable);
    
    /**
     * Find audit logs by entity type
     */
    Page<AuditLog> findByEntityType(String entityType, Pageable pageable);
    
    /**
     * Find audit logs by entity
     */
    List<AuditLog> findByEntityTypeAndEntityId(String entityType, UUID entityId);
    
    /**
     * Find audit logs by action
     */
    List<AuditLog> findByAction(String action);
    
    /**
     * Find audit logs by severity
     */
    Page<AuditLog> findBySeverity(SeverityLevel severity, Pageable pageable);
    
    // ===== TIME-BASED QUERIES =====
    
    /**
     * Find audit logs in time range
     */
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt BETWEEN :startTime AND :endTime " +
           "ORDER BY al.createdAt DESC")
    Page<AuditLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime,
                                  Pageable pageable);
    
    /**
     * Find recent audit logs
     */
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt > :since ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentLogs(@Param("since") LocalDateTime since);
    
    /**
     * Find audit logs for today
     */
    @Query("SELECT al FROM AuditLog al WHERE DATE(al.createdAt) = CURRENT_DATE ORDER BY al.createdAt DESC")
    List<AuditLog> findTodaysLogs();
    
    // ===== USER ACTIVITY TRACKING =====
    
    /**
     * Find user's recent activity
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId " +
           "AND al.createdAt > :since ORDER BY al.createdAt DESC")
    List<AuditLog> findUserRecentActivity(@Param("userId") UUID userId, @Param("since") LocalDateTime since);
    
    /**
     * Find user's activity by action type
     */
    @Query("SELECT al FROM AuditLog al WHERE al.user.id = :userId AND al.action = :action " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findUserActivityByAction(@Param("userId") UUID userId, @Param("action") String action);
    
    /**
     * Find login activities
     */
    @Query("SELECT al FROM AuditLog al WHERE al.action IN ('LOGIN', 'LOGOUT', 'LOGIN_FAILED') " +
           "ORDER BY al.createdAt DESC")
    Page<AuditLog> findLoginActivities(Pageable pageable);
    
    /**
     * Find failed login attempts
     */
    @Query("SELECT al FROM AuditLog al WHERE al.action = 'LOGIN_FAILED' " +
           "AND al.createdAt > :since ORDER BY al.createdAt DESC")
    List<AuditLog> findFailedLoginAttempts(@Param("since") LocalDateTime since);
    
    /**
     * Find suspicious login patterns
     */
    @Query("SELECT al.ipAddress, COUNT(al) as attemptCount FROM AuditLog al " +
           "WHERE al.action = 'LOGIN_FAILED' AND al.createdAt > :since " +
           "GROUP BY al.ipAddress HAVING COUNT(al) >= :threshold " +
           "ORDER BY attemptCount DESC")
    List<Object[]> findSuspiciousLoginPatterns(@Param("since") LocalDateTime since, 
                                              @Param("threshold") Long threshold);
    
    // ===== ENTITY CHANGE TRACKING =====
    
    /**
     * Find entity modification history
     */
    @Query("SELECT al FROM AuditLog al WHERE al.entityType = :entityType AND al.entityId = :entityId " +
           "AND al.action IN ('CREATE', 'UPDATE', 'DELETE') ORDER BY al.createdAt DESC")
    List<AuditLog> findEntityHistory(@Param("entityType") String entityType, @Param("entityId") UUID entityId);
    
    /**
     * Find recent data changes
     */
    @Query("SELECT al FROM AuditLog al WHERE al.action IN ('CREATE', 'UPDATE', 'DELETE') " +
           "AND al.createdAt > :since ORDER BY al.createdAt DESC")
    List<AuditLog> findRecentDataChanges(@Param("since") LocalDateTime since);
    
    /**
     * Find changes to specific field
     */
    @Query("SELECT al FROM AuditLog al WHERE :fieldName MEMBER OF al.changedFields " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findChangesByField(@Param("fieldName") String fieldName);
    
    /**
     * Find bulk operations
     */
    @Query("SELECT al FROM AuditLog al WHERE JSON_EXTRACT(al.metadata, '$.bulkOperation') = true " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findBulkOperations();
    
    // ===== SECURITY MONITORING =====
    
    /**
     * Find high severity events
     */
    @Query("SELECT al FROM AuditLog al WHERE al.severity IN ('HIGH', 'CRITICAL') " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findHighSeverityEvents();
    
    /**
     * Find critical security events
     */
    @Query("SELECT al FROM AuditLog al WHERE al.severity = 'CRITICAL' " +
           "AND al.createdAt > :since ORDER BY al.createdAt DESC")
    List<AuditLog> findCriticalEvents(@Param("since") LocalDateTime since);
    
    /**
     * Find privilege escalations
     */
    @Query("SELECT al FROM AuditLog al WHERE al.action = 'PRIVILEGE_CHANGE' " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findPrivilegeEscalations();
    
    /**
     * Find admin actions
     */
    @Query("SELECT al FROM AuditLog al WHERE JSON_EXTRACT(al.metadata, '$.adminAction') = true " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findAdminActions();
    
    // ===== COMPLIANCE AND REPORTING =====
    
    /**
     * Find audit logs by IP address
     */
    @Query("SELECT al FROM AuditLog al WHERE al.ipAddress = :ipAddress ORDER BY al.createdAt DESC")
    List<AuditLog> findByIpAddress(@Param("ipAddress") String ipAddress);
    
    /**
     * Find activities from specific session
     */
    @Query("SELECT al FROM AuditLog al WHERE al.sessionId = :sessionId ORDER BY al.createdAt ASC")
    List<AuditLog> findBySessionId(@Param("sessionId") String sessionId);
    
    /**
     * Find activities by request ID (for tracing)
     */
    @Query("SELECT al FROM AuditLog al WHERE al.requestId = :requestId ORDER BY al.createdAt ASC")
    List<AuditLog> findByRequestId(@Param("requestId") String requestId);
    
    /**
     * Generate compliance report for period
     */
    @Query("SELECT al.action, COUNT(al) as count, al.severity FROM AuditLog al " +
           "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY al.action, al.severity ORDER BY count DESC")
    List<Object[]> generateComplianceReport(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    // ===== ANALYTICS AND STATISTICS =====
    
    /**
     * Count logs by action type
     */
    @Query("SELECT al.action, COUNT(al) FROM AuditLog al " +
           "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY al.action ORDER BY COUNT(al) DESC")
    List<Object[]> countLogsByAction(@Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count logs by entity type
     */
    @Query("SELECT al.entityType, COUNT(al) FROM AuditLog al " +
           "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY al.entityType ORDER BY COUNT(al) DESC")
    List<Object[]> countLogsByEntityType(@Param("startDate") LocalDateTime startDate,
                                        @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count logs by user
     */
    @Query("SELECT u.email, COUNT(al) as activityCount FROM AuditLog al " +
           "JOIN User u ON al.user.id = u.id " +
           "WHERE al.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY u.id, u.email ORDER BY activityCount DESC")
    List<Object[]> countLogsByUser(@Param("startDate") LocalDateTime startDate,
                                  @Param("endDate") LocalDateTime endDate,
                                  Pageable pageable);
    
    /**
     * Get activity statistics by hour
     */
    @Query("SELECT EXTRACT(HOUR FROM al.createdAt) as hour, COUNT(al) as count " +
           "FROM AuditLog al WHERE DATE(al.createdAt) = :date " +
           "GROUP BY EXTRACT(HOUR FROM al.createdAt) ORDER BY hour")
    List<Object[]> getHourlyActivityStats(@Param("date") LocalDateTime date);
    
    /**
     * Get daily activity trends
     */
    @Query("SELECT DATE(al.createdAt) as date, COUNT(al) as count " +
           "FROM AuditLog al WHERE al.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(al.createdAt) ORDER BY date")
    List<Object[]> getDailyActivityTrends(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search audit logs by multiple criteria
     */
    @Query("SELECT al FROM AuditLog al WHERE " +
           "(:userId IS NULL OR al.user.id = :userId) " +
           "AND (:entityType IS NULL OR al.entityType = :entityType) " +
           "AND (:action IS NULL OR al.action = :action) " +
           "AND (:severity IS NULL OR al.severity = :severity) " +
           "AND (:ipAddress IS NULL OR al.ipAddress = :ipAddress) " +
           "AND al.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY al.createdAt DESC")
    Page<AuditLog> searchLogs(@Param("userId") UUID userId,
                             @Param("entityType") String entityType,
                             @Param("action") String action,
                             @Param("severity") SeverityLevel severity,
                             @Param("ipAddress") String ipAddress,
                             @Param("startDate") LocalDateTime startDate,
                             @Param("endDate") LocalDateTime endDate,
                             Pageable pageable);
    
    /**
     * Search by metadata content
     */
    @Query("SELECT al FROM AuditLog al WHERE JSON_CONTAINS(al.metadata, :searchValue) " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> searchByMetadata(@Param("searchValue") String searchValue);
    
    // ===== DATA RETENTION =====
    
    /**
     * Find old audit logs for cleanup
     */
    @Query("SELECT al FROM AuditLog al WHERE al.createdAt < :cutoffDate")
    List<AuditLog> findOldLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Count logs older than cutoff date
     */
    @Query("SELECT COUNT(al) FROM AuditLog al WHERE al.createdAt < :cutoffDate")
    Long countOldLogs(@Param("cutoffDate") LocalDateTime cutoffDate);
    
    /**
     * Find logs by severity for archival
     */
    @Query("SELECT al FROM AuditLog al WHERE al.severity IN :severities " +
           "AND al.createdAt < :cutoffDate")
    List<AuditLog> findLogsForArchival(@Param("severities") List<SeverityLevel> severities,
                                      @Param("cutoffDate") LocalDateTime cutoffDate);
    
    // ===== PERFORMANCE MONITORING =====
    
    /**
     * Find slow operations
     */
    @Query("SELECT al FROM AuditLog al WHERE JSON_EXTRACT(al.metadata, '$.executionTime') > :threshold " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findSlowOperations(@Param("threshold") Long threshold);
    
    /**
     * Find operations by execution time range
     */
    @Query("SELECT al FROM AuditLog al WHERE " +
           "JSON_EXTRACT(al.metadata, '$.executionTime') BETWEEN :minTime AND :maxTime " +
           "ORDER BY al.createdAt DESC")
    List<AuditLog> findOperationsByExecutionTime(@Param("minTime") Long minTime, 
                                                 @Param("maxTime") Long maxTime);
    
    /**
     * Get performance statistics
     */
    @Query("SELECT al.action, " +
           "AVG(CAST(JSON_EXTRACT(al.metadata, '$.executionTime') AS DECIMAL)) as avgTime, " +
           "MIN(CAST(JSON_EXTRACT(al.metadata, '$.executionTime') AS DECIMAL)) as minTime, " +
           "MAX(CAST(JSON_EXTRACT(al.metadata, '$.executionTime') AS DECIMAL)) as maxTime, " +
           "COUNT(al) as operationCount " +
           "FROM AuditLog al WHERE JSON_EXTRACT(al.metadata, '$.executionTime') IS NOT NULL " +
           "AND al.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY al.action ORDER BY avgTime DESC")
    List<Object[]> getPerformanceStatistics(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
}