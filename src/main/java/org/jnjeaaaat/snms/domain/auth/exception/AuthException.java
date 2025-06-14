package org.jnjeaaaat.snms.domain.auth.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;
import org.jnjeaaaat.snms.global.exception.ErrorCode;

public class AuthException extends CustomException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
