package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateJobStatusDto {
    @NotNull(message = "Job status is required")
    private JobStatus status;
}
