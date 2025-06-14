package org.jnjeaaaat.snms.global.security.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.domain.auth.entity.RedisToken;
import org.jnjeaaaat.snms.domain.auth.repository.RedisTokenRepository;
import org.jnjeaaaat.snms.domain.member.entity.Member;
import org.jnjeaaaat.snms.domain.member.entity.MemberProvider;
import org.jnjeaaaat.snms.domain.member.exception.MemberException;
import org.jnjeaaaat.snms.domain.member.repository.MemberProviderRepository;
import org.jnjeaaaat.snms.domain.member.repository.MemberRepository;
import org.jnjeaaaat.snms.global.security.jwt.JwtTokenProvider;
import org.jnjeaaaat.snms.global.util.CookieUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

import static org.jnjeaaaat.snms.global.constant.CookieCons.COOKIE_MAX_AGE;
import static org.jnjeaaaat.snms.global.constant.CookieCons.COOKIE_NAME;
import static org.jnjeaaaat.snms.global.exception.ErrorCode.NOT_FOUND_MEMBER;
import static org.jnjeaaaat.snms.global.exception.ErrorCode.NOT_FOUND_SOCIAL_LOGIN;
import static org.jnjeaaaat.snms.global.util.LogUtil.logInfo;

@Component
@RequiredArgsConstructor
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    @Value("${front.url}")
    private String frontRedirectUrl;

    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTokenRepository redisTokenRepository;
    private final MemberProviderRepository memberProviderRepository;
    private final MemberRepository memberRepository;

    @Transactional
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

        Member member = memberRepository.findById(Long.valueOf(authentication.getName()))
                .orElseThrow(() -> new MemberException(NOT_FOUND_MEMBER));

        MemberProvider memberProvider = memberProviderRepository.findByMember(member)
                .orElseThrow(() -> new MemberException(NOT_FOUND_SOCIAL_LOGIN));
        memberProvider.updateLastLogin();
        memberProviderRepository.save(memberProvider);

        CookieUtil.addCookie(response, COOKIE_NAME, accessToken, COOKIE_MAX_AGE);

        String redirectUrl = UriComponentsBuilder.fromUriString(frontRedirectUrl)
                .path("/oauth/success")
                .queryParam("token", accessToken)
                .build().toUriString();

        response.sendRedirect(redirectUrl);
    }
}
