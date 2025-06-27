package com.openrangelabs.middleware.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for Environment
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = EnvironmentValidator.class)
@Documented
public @interface ValidEnvironment {
    String message() default "Invalid environment. Must be one of: development, testing, staging, production (or their short forms: dev, test, stage, prod)";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}