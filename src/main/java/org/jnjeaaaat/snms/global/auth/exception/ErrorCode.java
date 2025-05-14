package org.jnjeaaaat.snms.global.auth.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(BAD_REQUEST, "유효하지 않은 요청입니다.");

    private final HttpStatus HttpStatus;
    private final String errorMessage;
}
