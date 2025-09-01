package com.workconnect.api.validation;

import com.workconnect.api.constants.Enum.JobType;
import com.workconnect.api.dto.CreateJobRequestDto;
import com.workconnect.api.dto.UpdateJobRequestDto;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalDate;

/**
 * Validator for job dates consistency.
 */
public class JobDatesValidator implements ConstraintValidator<ValidJobDates, Object> {

    @Override
    public void initialize(ValidJobDates constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Let @NotNull handle null validation
        }

        JobType jobType = null;
        LocalDate startDate = null;
        LocalDate endDate = null;

        if (value instanceof CreateJobRequestDto dto) {
            jobType = dto.getJobType();
            startDate = dto.getStartDate();
            endDate = dto.getEndDate();
        } else if (value instanceof UpdateJobRequestDto dto) {
            jobType = dto.getJobType();
            startDate = dto.getStartDate();
            endDate = dto.getEndDate();
        }

        if (jobType == null) {
            return true; // Let other validators handle this
        }

        // For CONTRACT jobs, validate date consistency
        if (jobType == JobType.CONTRACT) {
            if (startDate == null || endDate == null) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "Start date and end date are required for contract jobs")
                    .addConstraintViolation();
                return false;
            }
            
            if (!endDate.isAfter(startDate)) {
                context.disableDefaultConstraintViolation();
                context.buildConstraintViolationWithTemplate(
                    "End date must be after start date for contract jobs")
                    .addConstraintViolation();
                return false;
            }
        }

        return true;
    }
}
