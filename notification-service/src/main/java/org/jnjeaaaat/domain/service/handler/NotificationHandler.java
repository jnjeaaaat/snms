package org.jnjeaaaat.domain.service.handler;

import org.jnjeaaaat.global.event.NotificationEvent;
import org.jnjeaaaat.global.event.type.EventType;

public interface NotificationHandler {

    boolean supports(EventType eventType);
    void handle(NotificationEvent<?> event);

}
