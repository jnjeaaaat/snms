package org.jnjeaaaat.snms.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(BAD_REQUEST, "유효하지 않은 요청입니다."),
    DUPLICATE_EMAIL(BAD_REQUEST, "이미 가입된 이메일 입니다."),
    UNMATCHED_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다."),
    UNMATCHED_DEFAULT_FILE(BAD_REQUEST, "기본 프로필 사진이 아닙니다."),

    ;

    private final HttpStatus HttpStatus;
    private final String errorMessage;
}
