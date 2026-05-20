package com.karthik.urlshortener.analytics.entity;

import com.karthik.urlshortener.analytics.enums.DeviceType;
import com.karthik.urlshortener.common.entity.BaseEntity;
import com.karthik.urlshortener.url.entity.Url;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name = "analytics", indexes = {
        @Index(name = "idx_analytics_url_id", columnList = "url_id"),
        @Index(name = "idx_analytics_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Analytics extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ipAddress;
    private String browser;
    @Enumerated(EnumType.STRING)
    private DeviceType device;
    private String referer;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "url_id")
    private Url url;



}
