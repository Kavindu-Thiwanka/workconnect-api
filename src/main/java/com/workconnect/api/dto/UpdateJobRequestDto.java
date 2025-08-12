package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdateJobRequestDto {
    @NotBlank(message = "Job title is required")
    private String jobTitle;
    
    @NotBlank(message = "Job description is required")
    private String description;
    
    private String requiredSkills;
    
    @NotBlank(message = "Location is required")
    private String location;
    
    @NotNull(message = "Salary is required")
    private Double salary;
    
    @NotNull(message = "Job type is required")
    private JobType jobType;
    
    private LocalDate startDate;
    private LocalDate endDate;
}
