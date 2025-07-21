package org.jnjeaaaat.exception;

import org.jnjeaaaat.global.exception.CustomException;
import org.jnjeaaaat.global.exception.ErrorCode;

public class NotificationException extends CustomException {
    public NotificationException(ErrorCode errorCode) {
        super(errorCode);
    }

    public NotificationException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
