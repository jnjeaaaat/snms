package org.jnjeaaaat.domain.service.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.entity.Notification;
import org.jnjeaaaat.domain.event.BatchNotificationDispatchEvent;
import org.jnjeaaaat.domain.repository.NotificationRepository;
import org.jnjeaaaat.dto.member.FollowerInfoResponse;
import org.jnjeaaaat.global.client.member.MemberClient;
import org.jnjeaaaat.global.event.NotificationEvent;
import org.jnjeaaaat.global.event.dto.PostCreatedEventPayload;
import org.jnjeaaaat.global.event.type.EventType;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jnjeaaaat.global.constant.EventCons.POST_CREATED_EVENT_BODY_FORMAT;

@Slf4j
@Component
@RequiredArgsConstructor
public class PostCreatedNotificationHandler implements NotificationHandler {

    private final ObjectMapper objectMapper;
    private final MemberClient memberClient;
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Override
    public boolean supports(EventType eventType) {
        return EventType.POST_CREATED.equals(eventType);
    }

    @Override
    @Transactional
    public void handle(NotificationEvent<?> event) {
        PostCreatedEventPayload payload = objectMapper.convertValue(event.payload(), PostCreatedEventPayload.class);

        log.info(payload.memberUid());
        Long authorId = payload.memberId();
        EventType type = EventType.POST_CREATED;
        String body = String.format(POST_CREATED_EVENT_BODY_FORMAT, payload.memberUid());

        List<Long> receiverIds = memberClient.getFollowers(authorId)
                .stream()
                .map(FollowerInfoResponse::followerId)
                .toList();

        List<Notification> notifications = notificationRepository.saveAll(receiverIds.stream()
                .map(
                        receiverId -> Notification.builder()
                                .receiverId(receiverId)
                                .type(type)
                                .body(body)
                                .build()
                )
                .toList());

        eventPublisher.publishEvent(
                new BatchNotificationDispatchEvent(
                        notifications.stream()
                                .map(Notification::getId)
                                .toList()
                )
        );
    }

}
