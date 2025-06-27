package com.openrangelabs.middleware.validation;

import com.openrangelabs.middleware.logging.model.UserLogType;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidUserLogType
 */
public class UserLogTypeValidator implements ConstraintValidator<ValidUserLogType, String> {

    @Override
    public void initialize(ValidUserLogType constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null checks
        }
        return UserLogType.isValidCode(value);
    }
}