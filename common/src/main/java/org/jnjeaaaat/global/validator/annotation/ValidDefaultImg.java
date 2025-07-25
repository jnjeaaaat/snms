package org.jnjeaaaat.global.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.jnjeaaaat.global.validator.DefaultImgValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DefaultImgValidator.class)
public @interface ValidDefaultImg {

    String message() default "{validation.constraints.ValidDefaultImg.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
