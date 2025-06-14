package org.jnjeaaaat.snms.global.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.jnjeaaaat.snms.global.validator.ProviderNameValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ProviderNameValidator.class)
public @interface ValidProviderName {

    String message() default "{validation.constraints.ValidProviderName.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
