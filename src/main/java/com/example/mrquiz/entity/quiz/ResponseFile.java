package com.example.mrquiz.entity.quiz;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.entity.file.File;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "response_files", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"response_id", "file_id"}),
       indexes = @Index(name = "idx_response_files_response", columnList = "response_id"))
public class ResponseFile extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "response_id", nullable = false)
    private QuestionResponse response;
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "file_id", nullable = false)
    private File file;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "order_index")
    private Integer orderIndex = 0;
}