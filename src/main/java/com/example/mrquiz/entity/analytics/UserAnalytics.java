package com.example.mrquiz.entity.analytics;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.auth.User;
import com.example.mrquiz.entity.core.Course;
import com.example.mrquiz.entity.core.Institution;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "user_analytics", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "course_id", "subject_area", "period_start", "period_end"}),
       indexes = @Index(name = "idx_user_analytics_user_course", columnList = "user_id, course_id, period_start"))
public class UserAnalytics extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;
    
    // Time period for analytics
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;
    
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;
    
    @Column(name = "subject_area", length = 100)
    private String subjectArea;
    
    // Quiz statistics
    @Column(name = "total_quizzes_taken")
    private Integer totalQuizzesTaken = 0;
    
    @Column(name = "total_quizzes_completed")
    private Integer totalQuizzesCompleted = 0;
    
    @Column(name = "total_questions_answered")
    private Integer totalQuestionsAnswered = 0;
    
    @Column(name = "correct_answers")
    private Integer correctAnswers = 0;
    
    @Column(name = "partially_correct_answers")
    private Integer partiallyCorrectAnswers = 0;
    
    // Performance metrics
    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;
    
    @Column(name = "median_score", precision = 5, scale = 2)
    private BigDecimal medianScore;
    
    @Column(name = "best_score", precision = 5, scale = 2)
    private BigDecimal bestScore;
    
    @Column(name = "worst_score", precision = 5, scale = 2)
    private BigDecimal worstScore;
    
    @Column(name = "improvement_trend", precision = 5, scale = 4)
    private BigDecimal improvementTrend;
    
    @Column(name = "consistency_score", precision = 5, scale = 4)
    private BigDecimal consistencyScore;
    
    // Time analysis
    @Column(name = "total_time_spent")
    private Integer totalTimeSpent = 0;
    
    @Column(name = "average_time_per_quiz")
    private Integer averageTimePerQuiz;
    
    @Column(name = "time_efficiency_score", precision = 5, scale = 4)
    private BigDecimal timeEfficiencyScore;
    
    // Learning patterns
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_question_types", columnDefinition = "jsonb")
    private Map<String, Object> preferredQuestionTypes;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "strongest_topics", columnDefinition = "jsonb")
    private List<String> strongestTopics;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "weakest_topics", columnDefinition = "jsonb")
    private List<String> weakestTopics;
    
    @Column(name = "learning_velocity", precision = 5, scale = 4)
    private BigDecimal learningVelocity;
    
    // Engagement metrics
    @Column(name = "participation_rate", precision = 5, scale = 2)
    private BigDecimal participationRate;
    
    @Column(name = "completion_rate", precision = 5, scale = 2)
    private BigDecimal completionRate;
    
    @Column(name = "review_frequency")
    private Integer reviewFrequency = 0;
    
    @Column(name = "last_updated")
    private LocalDateTime lastUpdated = LocalDateTime.now();
}