package org.jnjeaaaat.global.security.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.global.security.oauth.exception.CustomOAuth2Exception;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.jnjeaaaat.global.util.LogUtil.logInfo;

@Component
@RequiredArgsConstructor
public class CustomOAuth2FailureHandler implements AuthenticationFailureHandler {

    @Value("${front.url}")
    private String frontRedirectUrl;

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        logInfo(request, "OAuth 로그인 실패 - 문자인증");

        if (exception instanceof CustomOAuth2Exception customOAuth2Ex) {

            String redirectUrl = UriComponentsBuilder.fromUriString(frontRedirectUrl)
                    .path("/oauth/phone")
                    .queryParam("providerName", customOAuth2Ex.getProviderName())
                    .queryParam("providerUserId", customOAuth2Ex.getProviderUserId())
                    .queryParam("email", customOAuth2Ex.getAttributes().get("email"))
                    .build().toUriString();

            response.sendRedirect(redirectUrl);
        }

    }
}
