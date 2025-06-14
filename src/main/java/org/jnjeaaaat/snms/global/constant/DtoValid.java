package org.jnjeaaaat.snms.global.constant;

import java.util.Set;
import java.util.regex.Pattern;

public class DtoValid {

    // todo: 사용하지 않는 message 삭제
    public static final String EMPTY_FIELD_MESSAGE = "비어있는 항목을 입력해주세요";
    public static final String PHONE_NUMBER_MESSAGE = "올바른 핸드폰 번호 형식이 아닙니다. (예: 01012341234)";
    public static final String PASSWORD_MESSAGE = "패스워드는 대문자, 소문자, 숫자, 특수기호를 포함한 8-12자리여야 합니다.";
    public static final String UID_MESSAGE = "아이디는 알파벳 소문자, 대문자, 숫자만 입력 가능합니다.";
    public static final String PROVIDER_NAME_MESSAGE = "지원하지 않는 소셜 로그인 제공자입니다. (google, kakao, naver만 지원)";
    public static final String AUTH_CODE_MESSAGE = "인증번호는 6자리 숫자 입니다.";

    public static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[!@#$%^&*])[A-Za-z\\d!@#$%^&*]{8,12}$";
    public static final String PHONE_REGEX = "^(010|011|016|017|018|019)\\d{7,8}$";
    public static final String UID_REGEX = "^[a-zA-Z0-9]+$";
    public static final String AUTH_CODE_REGEX = "^\\d{6}$";
    public static final String NICKNAME_REGEX = "^[a-zA-Z0-9가-힣]{1,10}$";
    public static final Set<String> VALID_PROVIDERS = Set.of("google", "kakao", "naver");
    public static final String DEFAULT_IMG_SUFFIX = "/default.jpg";

    public static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);
    public static final Pattern PHONE_PATTERN = Pattern.compile(PHONE_REGEX);
    public static final Pattern UID_PATTERN = Pattern.compile(UID_REGEX);
    public static final Pattern NICKNAME_PATTERN = Pattern.compile(NICKNAME_REGEX);
}
