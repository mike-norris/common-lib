package com.openrangelabs.middleware.validation;

import com.openrangelabs.middleware.logging.model.LogLevel;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidLogLevel
 */
public class LogLevelValidator implements ConstraintValidator<ValidLogLevel, String> {

    @Override
    public void initialize(ValidLogLevel constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null checks
        }
        try {
            LogLevel.fromString(value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}