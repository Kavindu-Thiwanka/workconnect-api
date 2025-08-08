package com.workconnect.api.dto.dashboard;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Employer Statistics DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerStatsDto {
    private int activeJobs;
    private int totalApplications;
    private int newApplicationsThisWeek;
    private int totalViews;
    private int totalJobsPosted;
    private double averageApplicationsPerJob;
}
