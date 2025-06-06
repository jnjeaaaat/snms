package org.jnjeaaaat.snms.domain.auth.entity;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "coolSms", timeToLive = 10 * 60)
public class RedisSms {

    private static final long AUTH_EXPIRE_TIME = 3 * 60;

    @Id
    private final String phoneNum;
    @Indexed
    private final String authCode;

    @TimeToLive
    private Long expiration;

    private boolean isVerified;

    public RedisSms(String phoneNum, String authCode) {
        this.phoneNum = phoneNum;
        this.authCode = authCode;
        this.expiration = AUTH_EXPIRE_TIME;
        this.isVerified = false;
    }

    public void verify() {
        this.isVerified = true;
    }
}
