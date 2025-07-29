package com.example.mrquiz.entity.auth;

import com.example.mrquiz.entity.BaseEntity;
import com.example.mrquiz.enums.TokenType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "auth_tokens", indexes = {
    @Index(name = "idx_token_hash", columnList = "tokenHash"),
    @Index(name = "idx_token_user_type", columnList = "user_id, tokenType")
})
public class AuthToken extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @Column(name = "token_hash", nullable = false)
    private String tokenHash;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false)
    private TokenType tokenType;
    
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;
    
    @Column(name = "used_at")
    private LocalDateTime usedAt;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> metadata;
}