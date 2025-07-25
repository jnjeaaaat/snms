package org.jnjeaaaat.domain.event;

import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.global.fcm.FcmDispatcher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final FcmDispatcher fcmDispatcher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleNotificationDispatch(NotificationDispatchEvent event) {
        fcmDispatcher.dispatch(event.notificationId());
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handleBatchNotificationDispatch(BatchNotificationDispatchEvent event) {
        event.notificationIds().forEach(fcmDispatcher::dispatch);
    }

}
