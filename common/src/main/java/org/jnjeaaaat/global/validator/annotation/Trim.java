package org.jnjeaaaat.global.validator.annotation;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.jnjeaaaat.global.validator.deserializer.TrimDeserializer;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@JsonDeserialize(using = TrimDeserializer.class)
public @interface Trim {
}
