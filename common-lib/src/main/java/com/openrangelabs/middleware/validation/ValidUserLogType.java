package com.openrangelabs.middleware.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for User Log Types
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UserLogTypeValidator.class)
@Documented
public @interface ValidUserLogType {
    String message() default "Invalid user log type. Must be one of: LOGIN, LOGOUT, CREATE, UPDATE, DELETE, VIEW, DOWNLOAD, UPLOAD, ERROR, AUDIT";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}