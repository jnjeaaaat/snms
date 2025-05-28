package org.jnjeaaaat.snms.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignInRequest;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignUpRequest;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignInResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.service.AuthService;
import org.jnjeaaaat.snms.global.util.CookieUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.jnjeaaaat.snms.global.constants.CookieCons.COOKIE_MAX_AGE;
import static org.jnjeaaaat.snms.global.constants.CookieCons.COOKIE_NAME;
import static org.jnjeaaaat.snms.global.util.LogUtil.logInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    // 로컬 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
            HttpServletRequest request,
            @RequestBody SignUpRequest signUpRequest) {

        logInfo(request, "회원가입 요청");

        SignUpResponse signUpResponse = authService.signUp(signUpRequest);

        URI location = UriComponentsBuilder.fromPath("/api/members/{id}").buildAndExpand(signUpResponse.id()).toUri();

        return ResponseEntity.created(location).body(signUpResponse);
    }

    // 로컬 로그인
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 @RequestBody SignInRequest signInRequest) {
        logInfo(request, "로그인 요청");

        SignInResponse signInResponse = authService.signIn(signInRequest);
        CookieUtil.addCookie(response, COOKIE_NAME, signInResponse.accessToken(), COOKIE_MAX_AGE);

        return ResponseEntity.ok(signInResponse);
    }
}
