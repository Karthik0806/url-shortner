package com.karthik.urlshortener.url.entity;

import com.karthik.urlshortener.common.entity.BaseEntity;
import com.karthik.urlshortener.user.entity.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "urls", indexes = {
        @Index(name = "idx_short_code", columnList = "short_code"),
        @Index(name = "idx_custom_alias", columnList = "custom_alias"),
        @Index(name = "idx_user_id", columnList = "user_id"),
        @Index(name = "idx_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "original_url", nullable = false)
    private String originalUrl;

    @Column(name = "short_code", unique = true)
    private String shortCode;

    @Builder.Default
    @Column(name = "click_count")
    private Long clickCount = 0L;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="user_id")
    private User user;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "custom_Alias", unique = true)
    private String customAlias;



}
