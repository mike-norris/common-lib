package com.openrangelabs.middleware.validation;

import com.openrangelabs.middleware.web.HttpMethod;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

/**
 * Validator implementation for ValidHttpMethod
 */
public class HttpMethodValidator implements ConstraintValidator<ValidHttpMethod, String> {

    @Override
    public void initialize(ValidHttpMethod constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null checks
        }
        return HttpMethod.isValidMethod(value);
    }
}