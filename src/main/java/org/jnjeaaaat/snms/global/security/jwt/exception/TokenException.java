package org.jnjeaaaat.snms.global.security.jwt.exception;

import org.jnjeaaaat.snms.global.exception.CustomException;
import org.jnjeaaaat.snms.global.exception.ErrorCode;

public class TokenException extends CustomException {

    public TokenException(ErrorCode errorCode) {
        super(errorCode);
    }
}
