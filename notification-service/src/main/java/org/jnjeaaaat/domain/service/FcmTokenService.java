package org.jnjeaaaat.domain.service;

import com.google.firebase.messaging.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.dto.request.FcmTokenRegisterRequest;
import org.jnjeaaaat.domain.entity.FcmToken;
import org.jnjeaaaat.domain.repository.FcmTokenRepository;
import org.jnjeaaaat.exception.NotificationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static org.jnjeaaaat.global.exception.ErrorCode.INTERNAL_ERROR;
import static org.jnjeaaaat.global.exception.ErrorCode.NOT_FOUND_FCM_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmTokenService {

    private final FcmTokenRepository fcmTokenRepository;
    private final FirebaseMessaging firebaseMessaging;

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

    public void send(Long targetUserId,
                     String title,
                     String body,
                     LocalDateTime createdAt,
                     Long notificationId) throws FirebaseMessagingException {
        FcmToken fcmToken = fcmTokenRepository.findById(targetUserId)
                .orElseThrow(() -> new NotificationException(NOT_FOUND_FCM_TOKEN));

        String token = fcmToken.getToken();

        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .setToken(token)
                .putData("title", title)
                .putData("body", body)
                .putData("createdAt", createdAt.toString())
                .putData("notificationId", notificationId.toString())
                .build();

        try {
            String response = firebaseMessaging.send(message);
            log.info("Successfully sent FCM message to userId: {}. Response: {}", targetUserId, response);
        } catch (FirebaseMessagingException e) {
            throw new NotificationException(INTERNAL_ERROR, e.getMessage());
        }

        log.info("FCM 알림 전송 요청: targetUserId={}, title={}, body={}", targetUserId, title, body);
    }
}
