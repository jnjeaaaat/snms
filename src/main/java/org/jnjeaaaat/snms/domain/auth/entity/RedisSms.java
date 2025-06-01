package org.jnjeaaaat.snms.domain.auth.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@RedisHash(value = "coolSms", timeToLive = 3 * 60)
public class RedisSms {

    @Id
    private final String phoneNum;

    private final String authCode;

    private boolean isVerified;

    public RedisSms(String phoneNum, String authCode) {
        this.phoneNum = phoneNum;
        this.authCode = authCode;
        this.isVerified = false;
    }

    public void verify() {
        this.isVerified = true;
    }
}
