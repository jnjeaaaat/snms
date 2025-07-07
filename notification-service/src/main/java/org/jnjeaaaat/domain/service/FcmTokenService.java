package org.jnjeaaaat.domain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.dto.request.FcmTokenRegisterRequest;
import org.jnjeaaaat.domain.entity.FcmToken;
import org.jnjeaaaat.domain.repository.FcmTokenRepository;
import org.jnjeaaaat.exception.NotificationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.jnjeaaaat.global.exception.ErrorCode.NOT_FOUND_FCM_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;

    @Transactional
    public void registerFcmToken(UserDetails userDetails,
                                 FcmTokenRegisterRequest request,
                                 String userAgent) {
        log.info("FCM 토큰 등록 요청: {}", request.token());

        Long memberId = Long.valueOf(userDetails.getUsername());

        FcmToken fcmToken = fcmTokenRepository
                .findByMemberIdAndUserAgent(memberId, userAgent)
                .orElse(null);

        if (fcmToken != null) {
            fcmToken.refreshToken(request.token());
        } else {
            fcmTokenRepository.save(FcmToken.builder()
                    .memberId(memberId)
                    .token(request.token())
                    .userAgent(userAgent)
                    .build());
        }
    }

    @Transactional
    public void deleteFcmToken(UserDetails userDetails, String userAgent) {
        log.info("FCM 토큰 삭제 요청: {}", userDetails.getUsername());

        Long memberId = Long.valueOf(userDetails.getUsername());

        FcmToken fcmToken = fcmTokenRepository.findByMemberIdAndUserAgent(memberId, userAgent)
                .orElseThrow(() -> new NotificationException(NOT_FOUND_FCM_TOKEN));

        fcmToken.deleteToken();
    }
}
