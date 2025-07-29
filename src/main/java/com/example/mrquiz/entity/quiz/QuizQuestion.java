package com.example.mrquiz.entity.quiz;

import com.example.mrquiz.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "quiz_questions", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"quiz_id", "order_index"}),
       indexes = @Index(name = "idx_quiz_questions_order", columnList = "quiz_id, order_index"))
public class QuizQuestion extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    // Position and grouping
    @Column(name = "order_index", nullable = false)
    private Integer orderIndex;
    
    @Column(length = 100)
    private String section; // Optional section grouping
    
    @Column(name = "page_number")
    private Integer pageNumber = 1;
    
    // Override settings
    @Column(precision = 5, scale = 2)
    private BigDecimal points; // Override default question points
    
    @Column(name = "time_limit")
    private Integer timeLimit; // Override default time limit for this question
    
    @Column(nullable = false)
    private Boolean required = true;
    
    // Question-specific settings in this quiz
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> settings;
}