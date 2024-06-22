package com.profITsoft.flightsystem.common.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * The annotation to validate a date.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DateValidator.class)
@Documented
public @interface ValidDate {
    String message() default "invalid.date.format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}