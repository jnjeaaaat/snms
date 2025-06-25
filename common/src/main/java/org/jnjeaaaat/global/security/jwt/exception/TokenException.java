package org.jnjeaaaat.global.security.jwt.exception;

import org.jnjeaaaat.global.exception.CustomException;
import org.jnjeaaaat.global.exception.ErrorCode;

public class TokenException extends CustomException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
