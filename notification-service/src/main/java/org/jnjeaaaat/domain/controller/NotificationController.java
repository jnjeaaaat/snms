package org.jnjeaaaat.domain.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.domain.dto.request.FcmTokenRegisterRequest;
import org.jnjeaaaat.domain.service.FcmTokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import static org.jnjeaaaat.global.util.LogUtil.logInfo;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notifications")
public class NotificationController {

    private final FcmTokenService fcmTokenService;

    @PostMapping("/register-token")
    public ResponseEntity<Void> registerFcmToken(HttpServletRequest request,
                                                 @AuthenticationPrincipal UserDetails userDetails,
                                                 @RequestBody @Valid FcmTokenRegisterRequest registerRequest,
                                                 @RequestHeader("User-Agent") String userAgent) {

        logInfo(request, "FCM 토큰 등록 요청");

        fcmTokenService.registerFcmToken(userDetails, registerRequest, userAgent);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/token")
    public ResponseEntity<Void> deleteFcmToken(HttpServletRequest request,
                                               @AuthenticationPrincipal UserDetails userDetails,
                                               @RequestHeader("User-Agent") String userAgent) {

        logInfo(request, "FCM 토큰 삭제 요청");

        fcmTokenService.deleteFcmToken(userDetails, userAgent);

        return ResponseEntity.ok().build();
    }
}
