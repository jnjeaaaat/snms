package org.jnjeaaaat.snms.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(BAD_REQUEST, "유효하지 않은 요청입니다."),
    DUPLICATE_UID(BAD_REQUEST, "이미 가입된 아이디 입니다."),
    UNMATCHED_PASSWORD(BAD_REQUEST, "재확인 비밀번호가 다릅니다."),
    UNMATCHED_DEFAULT_FILE(BAD_REQUEST, "기본 프로필 사진이 아닙니다."),

    NOT_FOUND_MEMBER(NOT_FOUND, "사용자를 찾을 수 없습니다."),
    WRONG_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus HttpStatus;
    private final String errorMessage;
}
