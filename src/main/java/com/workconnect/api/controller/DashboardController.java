package com.workconnect.api.controller;

import com.workconnect.api.dto.dashboard.*;
import com.workconnect.api.service.DashboardService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /**
     * Get worker dashboard statistics and data
     */
    @GetMapping("/worker")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<WorkerDashboardDto> getWorkerDashboard(Principal principal) {
        String userEmail = principal.getName();
        WorkerDashboardDto dashboard = dashboardService.getWorkerDashboard(userEmail);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get employer dashboard statistics and data
     */
    @GetMapping("/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployerDashboardDto> getEmployerDashboard(Principal principal) {
        String userEmail = principal.getName();
        EmployerDashboardDto dashboard = dashboardService.getEmployerDashboard(userEmail);
        return ResponseEntity.ok(dashboard);
    }

    /**
     * Get personalized job recommendations for worker
     */
    @GetMapping("/worker/recommendations")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<JobRecommendationsDto> getWorkerRecommendations(
            Principal principal,
            @RequestParam(defaultValue = "6") int limit) {
        String userEmail = principal.getName();
        JobRecommendationsDto recommendations = dashboardService.getWorkerRecommendations(userEmail, limit);
        return ResponseEntity.ok(recommendations);
    }

    /**
     * Get worker's recent job applications
     */
    @GetMapping("/worker/applications/recent")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<RecentApplicationsDto> getWorkerRecentApplications(
            Principal principal,
            @RequestParam(defaultValue = "5") int limit) {
        String userEmail = principal.getName();
        RecentApplicationsDto applications = dashboardService.getWorkerRecentApplications(userEmail, limit);
        return ResponseEntity.ok(applications);
    }

    /**
     * Get employer's active job postings
     */
    @GetMapping("/employer/jobs/active")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<ActiveJobsDto> getEmployerActiveJobs(
            Principal principal,
            @RequestParam(defaultValue = "5") int limit) {
        String userEmail = principal.getName();
        ActiveJobsDto activeJobs = dashboardService.getEmployerActiveJobs(userEmail, limit);
        return ResponseEntity.ok(activeJobs);
    }

    /**
     * Get recent applications for employer's jobs
     */
    @GetMapping("/employer/applications/recent")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<RecentApplicationsDto> getEmployerRecentApplications(
            Principal principal,
            @RequestParam(defaultValue = "5") int limit) {
        String userEmail = principal.getName();
        RecentApplicationsDto applications = dashboardService.getEmployerRecentApplications(userEmail, limit);
        return ResponseEntity.ok(applications);
    }

    /**
     * Get worker profile completion status
     */
    @GetMapping("/worker/profile-completion")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ProfileCompletionDto> getWorkerProfileCompletion(Principal principal) {
        String userEmail = principal.getName();
        ProfileCompletionDto completion = dashboardService.getWorkerProfileCompletion(userEmail);
        return ResponseEntity.ok(completion);
    }

    /**
     * Get worker statistics
     */
    @GetMapping("/worker/stats")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<WorkerStatsDto> getWorkerStats(Principal principal) {
        String userEmail = principal.getName();
        WorkerStatsDto stats = dashboardService.getWorkerStats(userEmail);
        return ResponseEntity.ok(stats);
    }

    /**
     * Get employer statistics
     */
    @GetMapping("/employer/stats")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployerStatsDto> getEmployerStats(Principal principal) {
        String userEmail = principal.getName();
        EmployerStatsDto stats = dashboardService.getEmployerStats(userEmail);
        return ResponseEntity.ok(stats);
    }
}
