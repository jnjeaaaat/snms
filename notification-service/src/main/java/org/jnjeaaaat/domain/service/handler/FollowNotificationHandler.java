package org.jnjeaaaat.domain.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.entity.Notification;
import org.jnjeaaaat.domain.event.NotificationDispatchEvent;
import org.jnjeaaaat.domain.repository.NotificationRepository;
import org.jnjeaaaat.global.event.NotificationEvent;
import org.jnjeaaaat.global.event.dto.FollowEventPayload;
import org.jnjeaaaat.global.event.type.EventType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static org.jnjeaaaat.global.constant.EventCons.FOLLOW_EVENT_BODY_FORMAT;

@Slf4j
@Component
@RequiredArgsConstructor
public class FollowNotificationHandler implements NotificationHandler {

    private final ObjectMapper objectMapper;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean supports(EventType eventType) {
        return EventType.FOLLOW.equals(eventType);
    }

    @Override
    @Transactional
    public void handle(NotificationEvent<?> event) {
        FollowEventPayload payload = objectMapper.convertValue(event.payload(), FollowEventPayload.class);

        Long receiverId = payload.followingId();
        EventType type = EventType.FOLLOW;
        String body = String.format(FOLLOW_EVENT_BODY_FORMAT, payload.followerUid());

        Notification notification = notificationRepository.save(Notification.builder()
                .receiverId(receiverId)
                .body(body)
                .type(type)
                .build()
        );

        eventPublisher.publishEvent(
                new NotificationDispatchEvent(notification.getId())
        );
    }
}
