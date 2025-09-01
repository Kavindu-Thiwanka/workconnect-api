package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.constants.Enum.JobType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminJobDto {
    private Long id;
    private String jobTitle;
    private String description;
    private String requiredSkills;
    private String location;
    private Double salary;
    private JobType jobType;
    private JobStatus status;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDateTime createdAt;
    private String employerEmail;
    private String employerCompanyName;
    private int totalApplications;
    private int approvedApplications;
    private int rejectedApplications;
    private int pendingApplications;
}
