package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobType;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobListingDto {
    private Long id;
    private String jobTitle;
    private String location;
    private Double salary;
    private JobType jobType;
    private String employerCompanyName;
    private LocalDateTime postedAt;
}
