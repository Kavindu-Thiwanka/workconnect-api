package com.workconnect.api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Validation annotation to ensure job dates are consistent.
 * For CONTRACT jobs, end date must be after start date.
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = JobDatesValidator.class)
@Documented
public @interface ValidJobDates {
    String message() default "Invalid job dates: end date must be after start date for contract jobs";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
