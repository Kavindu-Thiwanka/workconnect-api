package com.workconnect.api.dto;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateApplicationStatusDto {
    @NotNull
    private JobApplicationStatus status;
}
