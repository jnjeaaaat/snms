package org.jnjeaaaat.snms.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    INVALID_REQUEST(BAD_REQUEST, "유효하지 않은 요청입니다."),
    DUPLICATE_UID(BAD_REQUEST, "이미 가입된 아이디 입니다."),
    UNMATCHED_PASSWORD(BAD_REQUEST, "재확인 비밀번호가 다릅니다."),
    UNMATCHED_DEFAULT_FILE(BAD_REQUEST, "기본 프로필 사진이 아닙니다."),

    NOT_FOUND_PHONENUM(NOT_FOUND, "핸드폰 번호를 입력해주세요."),
    SMS_SEND_ERROR(INTERNAL_SERVER_ERROR, "문자 전송에 실패하였습니다."),
    ALREADY_VERIFIED_PHONENUM(BAD_REQUEST, "이미 인증된 핸드폰 번호입니다."),
    WRONG_AUTH_CODE(BAD_REQUEST, "틀린 인증번호 입니다."),
    NOT_FOUND_MEMBER(NOT_FOUND, "사용자를 찾을 수 없습니다."),
    WRONG_PASSWORD(BAD_REQUEST, "비밀번호가 일치하지 않습니다.");

    private final HttpStatus HttpStatus;
    private final String errorMessage;
}
