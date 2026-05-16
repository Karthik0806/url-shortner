package com.karthik.urlshortner.url.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "urls")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Url {
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

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @Column(name = "custom_Alias", unique = true)
    private String customAlias;

    @PrePersist
    public void prePersist(){
        this.createdAt = Instant.now();
    }

}
