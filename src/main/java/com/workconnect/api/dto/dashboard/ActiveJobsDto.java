package com.workconnect.api.dto.dashboard;

import com.workconnect.api.dto.JobListingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Active Jobs DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveJobsDto {
    private List<JobListingDto> jobs;
    private int totalCount;
}
