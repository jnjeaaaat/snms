package org.jnjeaaaat.snms.domain.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.domain.auth.dto.request.SignUpRequest;
import org.jnjeaaaat.snms.domain.auth.dto.response.SignUpResponse;
import org.jnjeaaaat.snms.domain.auth.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

import static org.jnjeaaaat.snms.global.util.LogUtils.logInfo;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/sign-up")
    public ResponseEntity<SignUpResponse> signUp(
            HttpServletRequest request,
            @RequestBody SignUpRequest signUpRequest) {

        logInfo(request, "회원가입 요청");

        SignUpResponse signUpResponse = authService.signUp(signUpRequest);

        URI location = UriComponentsBuilder.fromPath("/api/users/{id}").buildAndExpand(signUpResponse.id()).toUri();

        return ResponseEntity.created(location).body(signUpResponse);
    }
}
