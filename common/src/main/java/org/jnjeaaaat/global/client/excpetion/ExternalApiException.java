package org.jnjeaaaat.global.client.excpetion;

import org.jnjeaaaat.global.exception.CustomException;
import org.jnjeaaaat.global.exception.ErrorCode;

public class ExternalApiException extends CustomException {

    public ExternalApiException(ErrorCode errorCode) {
        super(errorCode);
    }

    public ExternalApiException(ErrorCode errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }
}
