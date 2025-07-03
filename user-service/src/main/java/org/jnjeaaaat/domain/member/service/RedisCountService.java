package org.jnjeaaaat.domain.member.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.member.type.CountType;
import org.jnjeaaaat.exception.MemberException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Slf4j
@Service
@RequiredArgsConstructor
public class RedisCountService {

    private final RedisTemplate<String, Object> redisTemplate;

    public void checkCount(Long loginMemberId, Long targetMemberId, CountType countType) {
        String key = "member:" + loginMemberId + ":" + countType.getType() + ":" + targetMemberId;
        Long count = redisTemplate.opsForValue().increment(key);

        if (count == 1) {
            redisTemplate.expire(key, Duration.ofMinutes(1));
        }

        if (count > 5) {
            throw new MemberException(countType.getLimitExceededError());
        }

    }
}
