package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;

import static org.jnjeaaaat.snms.global.exception.ErrorCode.WRONG_AUTH_CODE;

public class WrongAuthCodeException extends CustomException {

    public WrongAuthCodeException() {
        super(WRONG_AUTH_CODE);
    }
}
