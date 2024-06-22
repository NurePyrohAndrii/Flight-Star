package com.profITsoft.flightsystem.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.Instant;
import java.time.format.DateTimeParseException;

/**
 * DateValidator class is a validator class for ISO8601 date time format.
 */
public class DateValidator implements ConstraintValidator<ValidDate, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isEmpty()) {
            return true;
        }

        try {
            // Try to parse as Instant
            Instant.parse(value);
            return true;
        } catch (DateTimeParseException ignored) {
            // If failed, it's invalid
            return false;
        }
    }
}
