package org.jnjeaaaat.snms.global.auth.exception.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.jnjeaaaat.snms.global.auth.exception.ErrorCode;
import org.springframework.http.ResponseEntity;

@Getter
@RequiredArgsConstructor
public class ErrorResponse {

    private final ErrorCode errorCode;
    private final String message;

    public static ResponseEntity<ErrorResponse> of(ErrorCode errorCode, String message) {
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(new ErrorResponse(errorCode, message));
    }
}
