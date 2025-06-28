package org.jnjeaaaat.exception;

import org.jnjeaaaat.global.exception.CustomException;
import org.jnjeaaaat.global.exception.ErrorCode;

public class PostException extends CustomException {
    public PostException(ErrorCode errorCode) {
        super(errorCode);
    }
}
