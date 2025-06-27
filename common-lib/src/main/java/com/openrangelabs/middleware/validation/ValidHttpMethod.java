package com.openrangelabs.middleware.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

/**
 * Validation annotation for HTTP methods
 */
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HttpMethodValidator.class)
@Documented
public @interface ValidHttpMethod {
    String message() default "Invalid HTTP method. Must be one of: GET, POST, PUT, DELETE, PATCH, HEAD, OPTIONS, TRACE, CONNECT";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
