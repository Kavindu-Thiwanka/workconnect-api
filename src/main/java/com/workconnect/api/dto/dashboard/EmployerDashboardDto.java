package com.workconnect.api.dto.dashboard;

import com.workconnect.api.dto.JobApplicationDto;
import com.workconnect.api.dto.JobListingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Employer Dashboard DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployerDashboardDto {
    private int activeJobs;
    private int totalApplications;
    private int newApplicationsThisWeek;
    private int totalViews;
    private List<JobApplicationDto> recentApplications;
    private List<JobListingDto> activeJobPostings;
}
