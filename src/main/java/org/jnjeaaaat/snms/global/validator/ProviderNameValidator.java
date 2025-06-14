package org.jnjeaaaat.snms.global.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.jnjeaaaat.snms.global.validator.annotation.ValidProviderName;

import static org.jnjeaaaat.snms.global.constant.DtoValid.VALID_PROVIDERS;

public class ProviderNameValidator implements ConstraintValidator<ValidProviderName, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        return value == null || VALID_PROVIDERS.contains(value.toLowerCase());
    }

}
