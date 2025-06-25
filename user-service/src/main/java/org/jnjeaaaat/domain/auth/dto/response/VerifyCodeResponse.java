package org.jnjeaaaat.domain.auth.dto.response;

public record VerifyCodeResponse(
        String phoneNum,
        String accessToken,
        boolean needSignUp,
        String providerName,
        String providerUserId,
        String email
) {

}
