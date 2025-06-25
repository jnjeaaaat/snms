package org.jnjeaaaat.domain.auth.exception;

import org.jnjeaaaat.global.exception.CustomException;
import org.jnjeaaaat.global.exception.ErrorCode;

public class AuthException extends CustomException {

    public AuthException(ErrorCode errorCode) {
        super(errorCode);
    }

    public AuthException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
