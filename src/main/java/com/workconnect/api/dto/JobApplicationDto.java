package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String employerCompanyName;
    private JobApplicationStatus status;
    private LocalDateTime appliedAt;
    private ApplicantDto applicant;
}
