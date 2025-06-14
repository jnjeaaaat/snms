package org.jnjeaaaat.snms.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jnjeaaaat.snms.global.validator.annotation.ValidNickname;

import static org.jnjeaaaat.snms.global.constant.DtoValid.NICKNAME_PATTERN;

public class NicknameValidator implements ConstraintValidator<ValidNickname, String> {

    @Override
    public boolean isValid(String nickname, ConstraintValidatorContext context) {
        if (nickname == null) {
            return false;
        }

        if (nickname.trim().isEmpty()) {
            return false;
        }

        return NICKNAME_PATTERN.matcher(nickname).matches();
    }
}
