package org.jnjeaaaat.domain.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.entity.Notification;
import org.jnjeaaaat.domain.repository.NotificationRepository;
import org.jnjeaaaat.domain.service.FcmTokenService;
import org.jnjeaaaat.domain.type.NotificationStatus;
import org.jnjeaaaat.exception.NotificationException;
import org.jnjeaaaat.global.event.NotificationEvent;
import org.jnjeaaaat.global.event.dto.FollowEventPayload;
import org.jnjeaaaat.global.event.type.EventType;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.jnjeaaaat.global.constant.EventCons.FOLLOW_EVENT_BODY_FORMAT;
import static org.jnjeaaaat.global.exception.ErrorCode.INTERNAL_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowNotificationHandler implements NotificationHandler {

    private final FcmTokenService fcmTokenService;
    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;

    @Override
    public boolean supports(EventType eventType) {
        return eventType == EventType.FOLLOW;
    }

    @Override
    @Transactional
    public void handle(NotificationEvent<?> event) {
        FollowEventPayload payload = objectMapper.convertValue(event.payload(), FollowEventPayload.class);

        Long receiverId = payload.followingId();
        EventType type = EventType.FOLLOW;
        String body = String.format(FOLLOW_EVENT_BODY_FORMAT, payload.followerUid());

        Notification notification = Notification.builder()
                .receiverId(receiverId)
                .body(body)
                .type(type)
                .build();

        notificationRepository.save(notification);

        try {
            fcmTokenService.send(
                    receiverId,
                    type.getTitle(),
                    body,
                    notification.getCreatedAt(),
                    notification.getId()
            );
            notification.updateStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            notification.updateStatus(NotificationStatus.FAILED);
            throw new NotificationException(INTERNAL_ERROR, e.getMessage());
        }

    }
}
