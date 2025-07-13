package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class JobApplicationDto {
    private Long id;
    private JobApplicationStatus status;
    private LocalDateTime appliedAt;
    private ApplicantDto applicant;
}
