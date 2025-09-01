package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminApplicationDto {
    private Long id;
    private Long jobId;
    private String jobTitle;
    private String employerEmail;
    private String employerCompanyName;
    private Long workerId;
    private String workerEmail;
    private String workerName;
    private JobApplicationStatus status;
    private LocalDateTime appliedAt;
    private LocalDateTime statusUpdatedAt;
    private String coverLetter;
    private String resumeUrl;
}
