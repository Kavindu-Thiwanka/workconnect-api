package com.workconnect.api.service;

import com.workconnect.api.dto.dashboard.*;

public interface DashboardService {

    /**
     * Get comprehensive worker dashboard data
     */
    WorkerDashboardDto getWorkerDashboard(String userEmail);

    /**
     * Get comprehensive employer dashboard data
     */
    EmployerDashboardDto getEmployerDashboard(String userEmail);

    /**
     * Get personalized job recommendations for worker
     */
    JobRecommendationsDto getWorkerRecommendations(String userEmail, int limit);

    /**
     * Get worker's recent job applications
     */
    RecentApplicationsDto getWorkerRecentApplications(String userEmail, int limit);

    /**
     * Get employer's active job postings
     */
    ActiveJobsDto getEmployerActiveJobs(String userEmail, int limit);

    /**
     * Get recent applications for employer's jobs
     */
    RecentApplicationsDto getEmployerRecentApplications(String userEmail, int limit);

    /**
     * Get worker profile completion status
     */
    ProfileCompletionDto getWorkerProfileCompletion(String userEmail);

    /**
     * Get worker statistics
     */
    WorkerStatsDto getWorkerStats(String userEmail);

    /**
     * Get employer statistics
     */
    EmployerStatsDto getEmployerStats(String userEmail);
}
