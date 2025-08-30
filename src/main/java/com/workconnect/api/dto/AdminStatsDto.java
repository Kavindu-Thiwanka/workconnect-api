package com.workconnect.api.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AdminStatsDto {
    private long totalUsers;
    private long totalWorkers;
    private long totalEmployers;
    private long totalAdmins;
    private long activeUsers;
    private long inactiveUsers;
    private long bannedUsers;
    
    private long totalJobs;
    private long openJobs;
    private long closedJobs;
    private long filledJobs;
    private long expiredJobs;
    
    private long totalApplications;
    private long pendingApplications;
    private long approvedApplications;
    private long rejectedApplications;
    
    private long newUsersThisWeek;
    private long newJobsThisWeek;
    private long newApplicationsThisWeek;
    
    private double averageJobsPerEmployer;
    private double averageApplicationsPerJob;
    private double averageApplicationsPerWorker;
}
