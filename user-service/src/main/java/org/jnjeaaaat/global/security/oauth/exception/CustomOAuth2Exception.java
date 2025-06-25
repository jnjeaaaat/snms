package org.jnjeaaaat.global.security.oauth.exception;

import lombok.Getter;
import org.jnjeaaaat.global.exception.ErrorCode;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;

import java.util.Map;

@Getter
public class CustomOAuth2Exception extends OAuth2AuthenticationException {

    private final String providerName;
    private final String providerUserId;
    private final Map<String, Object> attributes;

    public CustomOAuth2Exception(ErrorCode errorCode,
                                 String providerName,
                                 String providerUserId,
                                 Map<String, Object> attributes) {
        super(errorCode.getErrorMessage());
        this.providerName = providerName;
        this.providerUserId = providerUserId;
        this.attributes = attributes;
    }

}
