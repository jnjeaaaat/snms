package org.jnjeaaaat.snms.domain.member.type;

import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.domain.auth.exception.AuthException;
import org.jnjeaaaat.snms.global.security.oauth.dto.OAuthUserInfo;

import java.util.Map;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.ILLEGAL_OAUTH;

@RequiredArgsConstructor
public enum LoginType {

    GOOGLE {
        @Override
        public OAuthUserInfo getUserInfo(Map<String, Object> attributes) {

            return OAuthUserInfo.builder()
                    .providerName(GOOGLE)
                    .providerUserId((String) attributes.get("sub"))
                    .build();
        }
    };

    // todo: KAKAO, NAVER

    public static LoginType getLoginTypeFromProviderName(String providerName) {
        return switch (providerName) {
            case "google" -> GOOGLE;
            default -> throw new AuthException(ILLEGAL_OAUTH);
        };

    }

    public abstract OAuthUserInfo getUserInfo(Map<String, Object> attributes);
}
