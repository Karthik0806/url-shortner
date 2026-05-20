package com.karthik.urlshortener.common.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RateLimitService {

    private static final long MAX_REQUESTS = 50;
    private static final Duration WINDOW = Duration.ofMinutes(1);
    private final StringRedisTemplate redisTemplate;

    public boolean isAllowed(String ipAddress) {

        String key = "rate_limit:" + ipAddress;

        Long requests = redisTemplate.opsForValue().increment(key);

        // FIRST REQUEST
        if (requests != null && requests == 1) {

            redisTemplate.expire(key, WINDOW);
        }

        boolean allowed = requests != null && requests <= MAX_REQUESTS;

        if (!allowed) {

            log.warn("Rate limit exceeded for IP: {}", ipAddress);
        }

        return allowed;
    }
}
