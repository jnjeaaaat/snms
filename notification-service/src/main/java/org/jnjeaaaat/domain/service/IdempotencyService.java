package org.jnjeaaaat.domain.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class IdempotencyService {

    private final StringRedisTemplate redisTemplate;
    private static final long EXPIRATION_DAYS = 3;
    private static final String IDEMPOTENCY_KEY_PREFIX = "idempotency:events:";
    private static final String PROCESSED_STATUS = "processed";

    public boolean isDuplicate(String eventId) {
        String key = IDEMPOTENCY_KEY_PREFIX + eventId;
        Boolean isNew = redisTemplate.opsForValue().setIfAbsent(key, PROCESSED_STATUS, Duration.ofDays(EXPIRATION_DAYS));

        return isNew == null || !isNew;
    }

}
