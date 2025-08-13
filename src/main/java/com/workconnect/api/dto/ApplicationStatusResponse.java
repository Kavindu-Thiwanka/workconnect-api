package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationStatusResponse {
    private boolean hasApplied;
    private Long applicationId;
    private JobApplicationStatus status;
}
