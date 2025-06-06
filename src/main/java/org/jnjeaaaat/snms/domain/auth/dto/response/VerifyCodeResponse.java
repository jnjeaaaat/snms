package org.jnjeaaaat.snms.domain.auth.dto.response;

public record VerifyCodeResponse(
        String phoneNum,
        String accessToken,
        boolean needSignUp,
        String providerName,
        String providerUserId,
        String email
) {

}
