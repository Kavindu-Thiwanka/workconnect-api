package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.constants.Enum.JobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobListingDto {
    private Long id;
    private String jobTitle;
    private String description;
    private String location;
    private Double salary;
    private JobType jobType;
    private String requiredSkills;
    private JobStatus status;
    private String employerCompanyName;
    private LocalDateTime postedAt;
    private Integer applicationCount;
}
