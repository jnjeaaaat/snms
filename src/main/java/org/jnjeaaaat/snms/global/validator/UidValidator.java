package org.jnjeaaaat.snms.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jnjeaaaat.snms.global.validator.annotation.ValidUid;

import static org.jnjeaaaat.snms.global.constant.DtoValid.UID_PATTERN;

public class UidValidator implements ConstraintValidator<ValidUid, String> {

    private int min;
    private int max;

    @Override
    public void initialize(ValidUid constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(String loginId, ConstraintValidatorContext context) {
        if (loginId == null) {
            addViolation(context, "아이디를 입력해주세요");
            return false;
        }

        // 길이 체크
        if (loginId.length() < min) {
            addViolation(context, String.format("아이디는 최소 %d자 이상이어야 합니다", min));
            return false;
        }

        if (loginId.length() > max) {
            addViolation(context, String.format("아이디는 최대 %d자까지 입력 가능합니다", max));
            return false;
        }

        if (!UID_PATTERN.matcher(loginId).matches()) {
            addViolation(context, "아이디는 알파벳 소문자, 대문자, 숫자만 입력 가능합니다");
            return false;
        }

        return true;
    }

    private void addViolation(ConstraintValidatorContext context, String message) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }
}
