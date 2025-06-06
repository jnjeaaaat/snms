package org.jnjeaaaat.snms.global.security.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.domain.auth.entity.RedisToken;
import org.jnjeaaaat.snms.domain.auth.repository.RedisTokenRepository;
import org.jnjeaaaat.snms.global.security.jwt.JwtTokenProvider;
import org.jnjeaaaat.snms.global.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.jnjeaaaat.snms.global.constants.CookieCons.COOKIE_MAX_AGE;
import static org.jnjeaaaat.snms.global.constants.CookieCons.COOKIE_NAME;
import static org.jnjeaaaat.snms.global.util.LogUtil.logInfo;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${front.url}")
    private String frontRedirectUrl ;

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenRepository redisTokenRepository;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        logInfo(request, "OAuth 로그인 성공");

        String accessToken = jwtTokenProvider.createAccessToken(authentication);
        String refreshToken = jwtTokenProvider.createRefreshToken(authentication);

        redisTokenRepository.save(new RedisToken(
                Long.valueOf(authentication.getName()),
                refreshToken,
                accessToken
        ));

        CookieUtil.addCookie(response, COOKIE_NAME, accessToken, COOKIE_MAX_AGE);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontRedirectUrl)
                .path("/oauth/success")
                .queryParam("token", accessToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
