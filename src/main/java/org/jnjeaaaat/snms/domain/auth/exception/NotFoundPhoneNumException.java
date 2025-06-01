package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.NOT_FOUND_PHONENUM;

public class NotFoundPhoneNumException extends CustomException {

    public NotFoundPhoneNumException() {
        super(NOT_FOUND_PHONENUM);
    }
}
