package com.workconnect.api.dto.dashboard;

import com.workconnect.api.dto.JobListingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Job Recommendations DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobRecommendationsDto {
    private List<JobListingDto> recommendations;
    private int totalCount;
    private String recommendationReason;
}
