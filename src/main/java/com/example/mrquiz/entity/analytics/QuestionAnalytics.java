package com.example.mrquiz.entity.analytics;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.core.Institution;
import com.example.mrquiz.entity.quiz.Question;
import com.example.mrquiz.entity.quiz.Quiz;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "question_analytics", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"question_id", "quiz_id", "period_start", "period_end"}),
       indexes = @Index(name = "idx_question_analytics_question", columnList = "question_id, period_start"))
public class QuestionAnalytics extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id")
    private Quiz quiz;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "institution_id")
    private Institution institution;
    
    // Time period for analytics
    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;
    
    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;
    
    // Basic statistics
    @Column(name = "total_attempts")
    private Integer totalAttempts = 0;
    
    @Column(name = "correct_attempts")
    private Integer correctAttempts = 0;
    
    @Column(name = "partially_correct_attempts")
    private Integer partiallyCorrectAttempts = 0;
    
    @Column(name = "incorrect_attempts")
    private Integer incorrectAttempts = 0;
    
    // Performance metrics
    @Column(name = "success_rate", precision = 5, scale = 4)
    private BigDecimal successRate;
    
    @Column(name = "average_score", precision = 5, scale = 2)
    private BigDecimal averageScore;
    
    @Column(name = "difficulty_index", precision = 5, scale = 4)
    private BigDecimal difficultyIndex;
    
    @Column(name = "discrimination_index", precision = 5, scale = 4)
    private BigDecimal discriminationIndex;
    
    // Time analysis
    @Column(name = "average_time_spent", precision = 10, scale = 2)
    private BigDecimal averageTimeSpent;
    
    @Column(name = "median_time_spent", precision = 10, scale = 2)
    private BigDecimal medianTimeSpent;
    
    @Column(name = "time_efficiency_score", precision = 5, scale = 4)
    private BigDecimal timeEfficiencyScore;
    
    // Answer pattern analysis
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "answer_distribution", columnDefinition = "jsonb")
    private Map<String, Object> answerDistribution;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "common_mistakes", columnDefinition = "jsonb")
    private Map<String, Object> commonMistakes;
    
    @Column(name = "last_calculated")
    private LocalDateTime lastCalculated = LocalDateTime.now();
}