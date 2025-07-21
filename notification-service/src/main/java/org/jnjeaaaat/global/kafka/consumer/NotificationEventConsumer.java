package org.jnjeaaaat.global.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jnjeaaaat.domain.service.NotificationService;
import org.jnjeaaaat.exception.NotificationException;
import org.jnjeaaaat.global.event.NotificationEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import static org.jnjeaaaat.global.constant.EventCons.*;
import static org.jnjeaaaat.global.exception.ErrorCode.INTERNAL_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventConsumer {

    private final ObjectMapper objectMapper;
    private final NotificationService notificationService;

    @KafkaListener(topics = {USER_EVENT_TOPIC, POST_EVENT_TOPIC}, groupId = EVENT_GROUP)
    public void consumeNotificationEvents(String message) {
        try {
            NotificationEvent<?> event = objectMapper.readValue(message, NotificationEvent.class);
            notificationService.processEvent(event);
        } catch (Exception e) {
            throw new NotificationException(INTERNAL_ERROR, e.getMessage());
        }
    }

}
