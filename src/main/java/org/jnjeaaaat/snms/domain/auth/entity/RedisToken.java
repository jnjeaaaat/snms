package org.jnjeaaaat.snms.domain.auth.entity;

import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;

@AllArgsConstructor
@RedisHash(value = "token", timeToLive = 60 * 60 * 24 * 7)
public class RedisToken {

    @Id
    private String email;
    private String refreshToken;
    @Indexed
    private String accessToken;
}
