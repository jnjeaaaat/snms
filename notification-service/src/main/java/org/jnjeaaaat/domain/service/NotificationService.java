package org.jnjeaaaat.domain.service;

import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.domain.service.handler.NotificationHandler;
import org.jnjeaaaat.global.event.NotificationEvent;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final List<NotificationHandler> notificationHandlers;
    private final IdempotencyService idempotencyService;

    public void processEvent(NotificationEvent<?> event) {
        if (idempotencyService.isDuplicate(event.eventId())) {
            return;
        }

        notificationHandlers.stream()
                .filter(handler -> handler.supports(event.eventType()))
                .findFirst()
                .ifPresent(handler -> handler.handle(event));
    }
}
