package com.openrangelabs.middleware.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for Log Levels
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = LogLevelValidator.class)
@Documented
public @interface ValidLogLevel {
    String message() default "Invalid log level. Must be one of: TRACE, DEBUG, INFO, WARN, ERROR, FATAL";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}