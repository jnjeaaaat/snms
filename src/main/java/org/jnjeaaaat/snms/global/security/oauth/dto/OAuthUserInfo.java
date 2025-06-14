package org.jnjeaaaat.snms.global.security.oauth.dto;

import lombok.Builder;
import org.jnjeaaaat.snms.domain.member.type.LoginType;

@Builder
public record OAuthUserInfo(
        LoginType providerName,
        String providerUserId
) {

}