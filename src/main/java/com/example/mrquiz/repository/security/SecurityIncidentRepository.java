package com.example.mrquiz.repository.security;

import com.example.mrquiz.entity.security.SecurityIncident;
import com.example.mrquiz.enums.IncidentType;
import com.example.mrquiz.enums.SeverityLevel;
import com.example.mrquiz.repository.BaseRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface SecurityIncidentRepository extends BaseRepository<SecurityIncident> {
    
    // ===== BASIC INCIDENT QUERIES =====
    
    /**
     * Find incidents by user
     */
    List<SecurityIncident> findByUserId(UUID userId);
    
    /**
     * Find incidents by type
     */
    List<SecurityIncident> findByIncidentType(IncidentType incidentType);
    
    /**
     * Find incidents by severity
     */
    Page<SecurityIncident> findBySeverity(SeverityLevel severity, Pageable pageable);
    
    /**
     * Find incidents by status
     */
    List<SecurityIncident> findByStatus(String status);
    
    /**
     * Find incidents by quiz attempt
     */
    List<SecurityIncident> findByAttemptId(UUID attemptId);
    
    /**
     * Find incidents by quiz session
     */
    List<SecurityIncident> findBySessionId(UUID sessionId);
    
    // ===== INCIDENT STATUS MANAGEMENT =====
    
    /**
     * Find open incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.status = 'open' ORDER BY si.createdAt DESC")
    List<SecurityIncident> findOpenIncidents();
    
    /**
     * Find incidents under investigation
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.status = 'investigating' ORDER BY si.createdAt DESC")
    List<SecurityIncident> findIncidentsUnderInvestigation();
    
    /**
     * Find unassigned incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.assignedTo IS NULL AND si.status = 'open' " +
           "ORDER BY si.severity DESC, si.createdAt DESC")
    List<SecurityIncident> findUnassignedIncidents();
    
    /**
     * Find incidents assigned to user
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.assignedTo.id = :userId " +
           "AND si.status NOT IN ('resolved', 'false_positive') ORDER BY si.createdAt DESC")
    List<SecurityIncident> findIncidentsAssignedToUser(@Param("userId") UUID userId);
    
    /**
     * Find resolved incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.status = 'resolved' " +
           "ORDER BY si.resolvedAt DESC")
    Page<SecurityIncident> findResolvedIncidents(Pageable pageable);
    
    // ===== SEVERITY AND PRIORITY =====
    
    /**
     * Find critical incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.severity = 'CRITICAL' " +
           "AND si.status NOT IN ('resolved', 'false_positive') ORDER BY si.createdAt DESC")
    List<SecurityIncident> findCriticalIncidents();
    
    /**
     * Find high priority incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.severity IN ('HIGH', 'CRITICAL') " +
           "AND si.status NOT IN ('resolved', 'false_positive') ORDER BY si.severity DESC, si.createdAt DESC")
    List<SecurityIncident> findHighPriorityIncidents();
    
    /**
     * Find incidents requiring immediate attention
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.severity = 'CRITICAL' " +
           "AND si.status = 'open' AND si.assignedTo IS NULL ORDER BY si.createdAt ASC")
    List<SecurityIncident> findIncidentsRequiringImmediateAttention();
    
    // ===== TIME-BASED QUERIES =====
    
    /**
     * Find recent incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.createdAt > :since ORDER BY si.createdAt DESC")
    List<SecurityIncident> findRecentIncidents(@Param("since") LocalDateTime since);
    
    /**
     * Find incidents in time range
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.createdAt BETWEEN :startTime AND :endTime " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findIncidentsInTimeRange(@Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime);
    
    /**
     * Find overdue incidents (open for too long)
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.status = 'open' " +
           "AND si.createdAt < :threshold ORDER BY si.createdAt ASC")
    List<SecurityIncident> findOverdueIncidents(@Param("threshold") LocalDateTime threshold);
    
    // ===== INCIDENT TYPE ANALYSIS =====
    
    /**
     * Find cheating incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.incidentType = 'CHEATING_DETECTED' " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findCheatingIncidents();
    
    /**
     * Find tab switching incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.incidentType = 'TAB_SWITCH' " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findTabSwitchingIncidents();
    
    /**
     * Find copy-paste incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.incidentType = 'COPY_PASTE' " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findCopyPasteIncidents();
    
    /**
     * Find multiple login incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.incidentType = 'MULTIPLE_LOGINS' " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findMultipleLoginIncidents();
    
    /**
     * Find time manipulation incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.incidentType = 'TIME_MANIPULATION' " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findTimeManipulationIncidents();
    
    // ===== USER PATTERN ANALYSIS =====
    
    /**
     * Find incidents by user with count
     */
    @Query("SELECT si.user.id, CONCAT(si.user.firstName, ' ', si.user.lastName) as userName, " +
           "COUNT(si) as incidentCount FROM SecurityIncident si " +
           "WHERE si.createdAt > :since GROUP BY si.user.id, si.user.firstName, si.user.lastName " +
           "ORDER BY incidentCount DESC")
    List<Object[]> findIncidentsByUserWithCount(@Param("since") LocalDateTime since);
    
    /**
     * Find repeat offenders
     */
    @Query("SELECT si.user.id, CONCAT(si.user.firstName, ' ', si.user.lastName) as userName, " +
           "COUNT(si) as incidentCount FROM SecurityIncident si " +
           "WHERE si.createdAt > :since GROUP BY si.user.id, si.user.firstName, si.user.lastName " +
           "HAVING COUNT(si) >= :threshold ORDER BY incidentCount DESC")
    List<Object[]> findRepeatOffenders(@Param("since") LocalDateTime since, @Param("threshold") Long threshold);
    
    /**
     * Find users with critical incidents
     */
    @Query("SELECT DISTINCT si.user FROM SecurityIncident si WHERE si.severity = 'CRITICAL' " +
           "AND si.createdAt > :since")
    List<Object> findUsersWithCriticalIncidents(@Param("since") LocalDateTime since);
    
    // ===== DETECTION AND CONFIDENCE =====
    
    /**
     * Find incidents by detection method
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.detectionMethod = :method " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findIncidentsByDetectionMethod(@Param("method") String method);
    
    /**
     * Find high confidence incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.confidenceScore >= :minConfidence " +
           "ORDER BY si.confidenceScore DESC, si.createdAt DESC")
    List<SecurityIncident> findHighConfidenceIncidents(@Param("minConfidence") Double minConfidence);
    
    /**
     * Find low confidence incidents (potential false positives)
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.confidenceScore < :maxConfidence " +
           "AND si.status = 'open' ORDER BY si.confidenceScore ASC")
    List<SecurityIncident> findLowConfidenceIncidents(@Param("maxConfidence") Double maxConfidence);
    
    /**
     * Find AI-detected incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.detectedBy = 'ai' " +
           "ORDER BY si.confidenceScore DESC, si.createdAt DESC")
    List<SecurityIncident> findAiDetectedIncidents();
    
    // ===== QUIZ AND SESSION ANALYSIS =====
    
    /**
     * Find incidents by quiz
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.attempt.quiz.id = :quizId " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findIncidentsByQuiz(@Param("quizId") UUID quizId);
    
    /**
     * Get quiz security statistics
     */
    @Query("SELECT q.id, q.title, COUNT(si) as incidentCount, " +
           "COUNT(DISTINCT si.user.id) as affectedUsers " +
           "FROM SecurityIncident si JOIN si.attempt.quiz q " +
           "WHERE si.createdAt > :since " +
           "GROUP BY q.id, q.title ORDER BY incidentCount DESC")
    List<Object[]> getQuizSecurityStatistics(@Param("since") LocalDateTime since);
    
    /**
     * Find session incidents
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.session.id = :sessionId " +
           "ORDER BY si.createdAt DESC")
    List<SecurityIncident> findSessionIncidents(@Param("sessionId") UUID sessionId);
    
    // ===== BULK OPERATIONS =====
    
    /**
     * Assign incident to user
     */
    @Modifying
    @Query("UPDATE SecurityIncident si SET si.assignedTo.id = :assigneeId, si.status = 'investigating' " +
           "WHERE si.id = :incidentId")
    void assignIncident(@Param("incidentId") UUID incidentId, @Param("assigneeId") UUID assigneeId);
    
    /**
     * Update incident status
     */
    @Modifying
    @Query("UPDATE SecurityIncident si SET si.status = :status WHERE si.id = :incidentId")
    void updateIncidentStatus(@Param("incidentId") UUID incidentId, @Param("status") String status);
    
    /**
     * Resolve incident
     */
    @Modifying
    @Query("UPDATE SecurityIncident si SET si.status = 'resolved', si.resolvedBy.id = :resolvedById, " +
           "si.resolvedAt = :resolvedAt, si.resolutionNotes = :notes WHERE si.id = :incidentId")
    void resolveIncident(@Param("incidentId") UUID incidentId,
                        @Param("resolvedById") UUID resolvedById,
                        @Param("resolvedAt") LocalDateTime resolvedAt,
                        @Param("notes") String notes);
    
    /**
     * Mark as false positive
     */
    @Modifying
    @Query("UPDATE SecurityIncident si SET si.status = 'false_positive', si.resolvedBy.id = :resolvedById, " +
           "si.resolvedAt = :resolvedAt WHERE si.id = :incidentId")
    void markAsFalsePositive(@Param("incidentId") UUID incidentId,
                            @Param("resolvedById") UUID resolvedById,
                            @Param("resolvedAt") LocalDateTime resolvedAt);
    
    /**
     * Bulk assign incidents
     */
    @Modifying
    @Query("UPDATE SecurityIncident si SET si.assignedTo.id = :assigneeId, si.status = 'investigating' " +
           "WHERE si.id IN :incidentIds")
    void bulkAssignIncidents(@Param("incidentIds") List<UUID> incidentIds, @Param("assigneeId") UUID assigneeId);
    
    // ===== ANALYTICS AND REPORTING =====
    
    /**
     * Count incidents by type
     */
    @Query("SELECT si.incidentType, COUNT(si) FROM SecurityIncident si " +
           "WHERE si.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY si.incidentType ORDER BY COUNT(si) DESC")
    List<Object[]> countIncidentsByType(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);
    
    /**
     * Count incidents by severity
     */
    @Query("SELECT si.severity, COUNT(si) FROM SecurityIncident si " +
           "WHERE si.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY si.severity ORDER BY COUNT(si) DESC")
    List<Object[]> countIncidentsBySeverity(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get incident resolution statistics
     */
    @Query("SELECT si.status, COUNT(si) as count, " +
           "AVG(CASE WHEN si.resolvedAt IS NOT NULL THEN " +
           "    EXTRACT(EPOCH FROM (si.resolvedAt - si.createdAt))/3600 END) as avgResolutionHours " +
           "FROM SecurityIncident si WHERE si.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY si.status")
    List<Object[]> getIncidentResolutionStats(@Param("startDate") LocalDateTime startDate,
                                             @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get daily incident trends
     */
    @Query("SELECT DATE(si.createdAt) as date, COUNT(si) as count " +
           "FROM SecurityIncident si WHERE si.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY DATE(si.createdAt) ORDER BY date")
    List<Object[]> getDailyIncidentTrends(@Param("startDate") LocalDateTime startDate,
                                         @Param("endDate") LocalDateTime endDate);
    
    /**
     * Get incident detection effectiveness
     */
    @Query("SELECT si.detectedBy, si.detectionMethod, COUNT(si) as count, " +
           "AVG(si.confidenceScore) as avgConfidence, " +
           "COUNT(CASE WHEN si.status = 'false_positive' THEN 1 END) as falsePositives " +
           "FROM SecurityIncident si WHERE si.createdAt BETWEEN :startDate AND :endDate " +
           "GROUP BY si.detectedBy, si.detectionMethod")
    List<Object[]> getDetectionEffectiveness(@Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate);
    
    // ===== SEARCH AND FILTERING =====
    
    /**
     * Search incidents with multiple criteria
     */
    @Query("SELECT si FROM SecurityIncident si WHERE " +
           "(:userId IS NULL OR si.user.id = :userId) " +
           "AND (:incidentType IS NULL OR si.incidentType = :incidentType) " +
           "AND (:severity IS NULL OR si.severity = :severity) " +
           "AND (:status IS NULL OR si.status = :status) " +
           "AND (:assignedTo IS NULL OR si.assignedTo.id = :assignedTo) " +
           "AND si.createdAt BETWEEN :startDate AND :endDate " +
           "ORDER BY si.createdAt DESC")
    Page<SecurityIncident> searchIncidents(@Param("userId") UUID userId,
                                          @Param("incidentType") IncidentType incidentType,
                                          @Param("severity") SeverityLevel severity,
                                          @Param("status") String status,
                                          @Param("assignedTo") UUID assignedTo,
                                          @Param("startDate") LocalDateTime startDate,
                                          @Param("endDate") LocalDateTime endDate,
                                          Pageable pageable);
    
    /**
     * Find incidents requiring follow-up
     */
    @Query("SELECT si FROM SecurityIncident si WHERE si.followUpRequired = true " +
           "AND si.status = 'resolved' ORDER BY si.resolvedAt DESC")
    List<SecurityIncident> findIncidentsRequiringFollowUp();
}