package org.jnjeaaaat.global.security.oauth.dto;

import lombok.Builder;
import org.jnjeaaaat.domain.member.type.LoginType;

@Builder
public record OAuthUserInfo(
        LoginType providerName,
        String providerUserId
) {

}