package org.jnjeaaaat.global.exception.dto;

import org.jnjeaaaat.global.exception.ErrorCode;
import org.springframework.http.ResponseEntity;

public record ErrorResponse(
        ErrorCode errorCode,
        String message
) {

    public static ResponseEntity<ErrorResponse> of(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(new ErrorResponse(errorCode, message));
    }
}
