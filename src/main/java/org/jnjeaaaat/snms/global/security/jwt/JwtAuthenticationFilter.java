package org.jnjeaaaat.snms.global.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.snms.global.security.jwt.exception.TokenException;
import org.jnjeaaaat.snms.global.util.CookieUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;

import static org.jnjeaaaat.snms.global.constant.CookieCons.COOKIE_MAX_AGE;
import static org.jnjeaaaat.snms.global.constant.CookieCons.COOKIE_NAME;
import static org.jnjeaaaat.snms.global.exception.ErrorCode.EMPTY_TOKEN;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String[] WHITELIST = {
            "/docs/**", // spring REST docs
            "/error",
            "/favicon.ico",
            "/api/auth/check-uid",
            "/api/auth/sign-up", // 회원가입
            "/api/auth/sign-in", // 로그인
            "/api/auth/send-sms", // 인증번호 발송
//            "/api/auth/send-sms-test", // 인증번호 발송 (로컬 테스트용)
            "/api/auth/oauth/success", // 소셜 로그인 성공
            "/api/auth/verify-code", // 인증번호 확인
            "/api/auth/oauth/phone",
            "/",
    };

    private final JwtTokenProvider jwtTokenProvider;
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();

        return Arrays.stream(WHITELIST)
                .anyMatch(pattern -> pathMatcher.match(pattern, path));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String accessToken = resolveToken(request);

        validateAndReissueToken(accessToken, response);

        filterChain.doFilter(request, response);
    }

    private String resolveToken(HttpServletRequest request) {
        return CookieUtil.getCookie(request, COOKIE_NAME)
                .map(Cookie::getValue)
                .orElseThrow(() -> new TokenException(EMPTY_TOKEN));
    }

    private void validateAndReissueToken(String accessToken, HttpServletResponse response) {
        if (jwtTokenProvider.validateToken(accessToken)) {
            setAuthentication(accessToken);
        } else {
            String reissueAccessToken = jwtTokenProvider.reissueToken(accessToken);
            if (reissueAccessToken != null) {
                setAuthentication(reissueAccessToken);
                CookieUtil.addCookie(response, COOKIE_NAME, reissueAccessToken, COOKIE_MAX_AGE);
            }
        }
    }

    private void setAuthentication(String accessToken) {
        Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
