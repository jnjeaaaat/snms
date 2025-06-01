package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.ALREADY_VERIFIED_PHONENUM;

public class AlreadyVerifiedPhoneNumException extends CustomException {

    public AlreadyVerifiedPhoneNumException() {
        super(ALREADY_VERIFIED_PHONENUM);
    }
}
