package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.SMS_SEND_ERROR;

public class SmsSendException extends CustomException {

    public SmsSendException() {
        super(SMS_SEND_ERROR);
    }
}
