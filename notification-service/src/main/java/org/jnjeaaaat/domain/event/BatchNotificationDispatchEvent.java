package org.jnjeaaaat.domain.event;

import java.util.List;

public record BatchNotificationDispatchEvent(
        List<Long> notificationIds
) {
}
