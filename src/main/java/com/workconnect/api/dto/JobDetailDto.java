package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobType;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class JobDetailDto {
    private Long id;
    private String jobTitle;
    private String description;
    private String requiredSkills;
    private String location;
    private Double salary;
    private JobType jobType;
    private String employerCompanyName;
    private LocalDateTime postedAt;

    // Fields specific to job types
    private LocalDate jobDate; // For ONE_DAY
    private LocalDate startDate; // For CONTRACT
    private LocalDate endDate;   // For CONTRACT
}
