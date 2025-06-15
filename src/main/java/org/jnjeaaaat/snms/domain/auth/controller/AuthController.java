package org.jnjeaaaat.snms.domain.auth.controller;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.domain.auth.dto.request.*;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignInResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.dto.response.VerifyCodeResponse;
import org.jnjeaaaat.snms.domain.auth.service.AuthService;
import org.jnjeaaaat.snms.domain.auth.service.CoolSmsService;
import org.jnjeaaaat.snms.global.util.CookieUtil;
import org.jnjeaaaat.snms.global.validator.annotation.ValidProviderName;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.jnjeaaaat.snms.global.constant.CookieCons.COOKIE_MAX_AGE;
import static org.jnjeaaaat.snms.global.constant.CookieCons.COOKIE_NAME;
import static org.jnjeaaaat.snms.global.util.LogUtil.logInfo;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final CoolSmsService coolSmsService;

    // 로컬 회원가입
    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) @ValidProviderName String providerName,
            @RequestParam(required = false) @Nullable String providerUserId,
            @RequestParam(required = false) @Nullable @Email String email,
            @RequestBody @Valid SignUpRequest signUpRequest) {

        logInfo(request, "회원가입 요청");

        SignUpResponse signUpResponse = authService.signUp(signUpRequest, providerName, providerUserId, email);
        CookieUtil.addCookie(response, COOKIE_NAME, signUpResponse.accessToken(), COOKIE_MAX_AGE);

        URI location = UriComponentsBuilder.fromPath("/api/members/{id}").buildAndExpand(signUpResponse.id()).toUri();

        return ResponseEntity.created(location).body(signUpResponse);
    }

    // 로컬 로그인
    @PostMapping("/sign-in")
    public ResponseEntity<SignInResponse> signIn(HttpServletRequest request,
                                                 HttpServletResponse response,
                                                 @RequestBody @Valid SignInRequest signInRequest) {
        logInfo(request, "로그인 요청");

        SignInResponse signInResponse = authService.signIn(signInRequest);
        CookieUtil.addCookie(response, COOKIE_NAME, signInResponse.accessToken(), COOKIE_MAX_AGE);

        return ResponseEntity.ok(signInResponse);
    }

    // 로그아웃
    @DeleteMapping("/sign-out")
    public ResponseEntity<Void> singOut(HttpServletRequest request,
                                        HttpServletResponse response,
                                        @AuthenticationPrincipal UserDetails userDetails) {

        logInfo(request, "로그아웃 요청");

        authService.signOut(userDetails);
        CookieUtil.deleteCookie(response, COOKIE_NAME);
        return ResponseEntity.ok().build();
    }

    // 핸드폰 인증 번호 요청
    @PostMapping("/send-sms")
    public ResponseEntity<Void> sendSms(HttpServletRequest request,
                                        @RequestBody @Valid SmsSendRequest smsSendRequest) {
        logInfo(request, "핸드폰 번호 인증 요청");

        coolSmsService.sendSms(request, smsSendRequest);

        return ResponseEntity.ok().build();
    }

    /**
     * needSignUp: true 일때 /api/auth/sign-up 회원가입 진행
     *
     * @param request
     * @param smsVerifyRequest
     * @return
     */
    // 인증 번호 확인
    @PostMapping("/verify-code")
    public ResponseEntity<VerifyCodeResponse> verifyAuthCode(
            HttpServletRequest request,
            HttpServletResponse response,
            @RequestParam(required = false) @ValidProviderName String providerName,
            @RequestParam(required = false) @Nullable String providerUserId,
            @RequestParam(required = false) @Nullable @Email String email,
            @RequestBody @Valid SmsVerifyRequest smsVerifyRequest) {

        logInfo(request, "인증번호 입력");

        VerifyCodeResponse verifyCodeResponse = authService.verifyAuthCode(smsVerifyRequest, providerName, providerUserId, email);
        CookieUtil.addCookie(response, COOKIE_NAME, verifyCodeResponse.accessToken(), COOKIE_MAX_AGE);

        return ResponseEntity.ok(verifyCodeResponse);
    }

    // 중복 아이디 체크
    @PostMapping("/check-uid")
    public ResponseEntity<Void> checkUid(HttpServletRequest request,
                                         @RequestBody @Valid UidCheckRequest uidCheckRequest) {

        logInfo(request, "아이디 중복 확인 요청");

        authService.checkUid(uidCheckRequest);
        return ResponseEntity.ok().build();
    }

    // 소셜 로그인 성공
    @GetMapping(value = "/oauth/success", produces = "text/plain;charset=UTF-8")
    public ResponseEntity<String> oauthSuccess() {
        return ResponseEntity.ok("성공 success social login");
    }
}
