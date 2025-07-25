package org.jnjeaaaat.global.fcm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.entity.Notification;
import org.jnjeaaaat.domain.repository.NotificationRepository;
import org.jnjeaaaat.domain.service.FcmTokenService;
import org.jnjeaaaat.domain.type.NotificationStatus;
import org.jnjeaaaat.exception.NotificationException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.jnjeaaaat.global.exception.ErrorCode.NOT_FOUND_NOTIFICATION;

@Slf4j
@Service
@RequiredArgsConstructor
public class FcmDispatcher {

    private final FcmTokenService fcmTokenService;
    private final NotificationRepository notificationRepository;

    @Async("fcmThreadPoolTaskExecutor")
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void dispatch(Long notificationId) {

        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotificationException(NOT_FOUND_NOTIFICATION));

        try {
            fcmTokenService.send(
                    notification.getReceiverId(),
                    notification.getType().getTitle(),
                    notification.getBody(),
                    notification.getCreatedAt(),
                    notification.getId()
            );

            notification.updateStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            log.error("FCM 전송 실패: {}, 실패 notification id : {}", e.getMessage(), notificationId, e);
            notification.updateStatus(NotificationStatus.FAILED);
        }
    }
}
