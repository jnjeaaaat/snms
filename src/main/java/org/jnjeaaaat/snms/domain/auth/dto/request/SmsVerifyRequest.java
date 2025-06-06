package org.jnjeaaaat.snms.domain.auth.dto.request;

public record SmsVerifyRequest(
        String phoneNum,

        String authCode,

        String providerName,

        String providerUserId,

        String email
) {

}
