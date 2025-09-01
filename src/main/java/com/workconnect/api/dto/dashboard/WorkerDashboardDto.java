package com.workconnect.api.dto.dashboard;

import com.workconnect.api.dto.JobApplicationDto;
import com.workconnect.api.dto.JobListingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Worker Dashboard DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkerDashboardDto {
    private int totalApplications;
    private int pendingApplications;
    private int interviewsScheduled;
    private int profileViews;
    private double profileCompletionPercentage;
    private List<JobApplicationDto> recentApplications;
    private List<JobListingDto> recommendedJobs;
    private List<String> profileCompletionTips;
}
