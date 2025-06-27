package com.openrangelabs.middleware.validation;

import com.openrangelabs.middleware.config.Environment;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidEnvironment
 */
public class EnvironmentValidator implements ConstraintValidator<ValidEnvironment, String> {

    @Override
    public void initialize(ValidEnvironment constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null checks
        }
        return Environment.isValidEnvironment(value);
    }
}