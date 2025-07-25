package org.jnjeaaaat.global.event;

import lombok.Builder;
import org.jnjeaaaat.global.event.type.EventType;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationEvent<T>(
        String eventId,
        EventType eventType,
        String sourceService,
        LocalDateTime createdAt,
        T payload
) {

    public static <T> NotificationEvent<T> of(T payload, EventType eventType, String sourceService) {
        return NotificationEvent.<T>builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .sourceService(sourceService)
                .createdAt(LocalDateTime.now())
                .payload(payload)
                .build();
    }
}
