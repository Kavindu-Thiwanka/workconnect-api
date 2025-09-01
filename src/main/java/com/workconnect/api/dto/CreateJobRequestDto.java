package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreateJobRequestDto {
    @NotBlank
    private String jobTitle;
    @NotBlank
    private String description;
    private String requiredSkills;
    @NotBlank
    private String location;
    @NotNull
    private Double salary;
    @NotNull
    private JobType jobType;
    private LocalDate startDate;
    private LocalDate endDate;
}
