package com.workconnect.api.controller;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.dto.JobApplicationDto;
import com.workconnect.api.dto.UpdateApplicationStatusDto;
import com.workconnect.api.entity.JobApplication;
import com.workconnect.api.service.BadgeService;
import com.workconnect.api.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/employer")
@PreAuthorize("hasRole('EMPLOYER')")
public class EmployerController {

    private final JobService jobService;
    private final BadgeService badgeService;

    public EmployerController(JobService jobService, BadgeService badgeService) {
        this.jobService = jobService;
        this.badgeService = badgeService;
    }

    @GetMapping("/jobs/{jobId}/applications")
    public ResponseEntity<List<JobApplicationDto>> getJobApplications(
            @PathVariable Long jobId,
            Principal principal) {
        List<JobApplicationDto> applications = jobService.getApplicationsForJob(principal.getName(), jobId);
        return ResponseEntity.ok(applications);
    }

    @PutMapping("/applications/{applicationId}/status")
    public ResponseEntity<Void> updateApplicationStatus(
            @PathVariable Long applicationId,
            @Valid @RequestBody UpdateApplicationStatusDto statusDto,
            Principal principal) {
        JobApplication application = jobService.updateApplicationStatus(principal.getName(), applicationId, statusDto.getStatus());

        if (application.getStatus() == JobApplicationStatus.COMPLETED) {
            badgeService.checkAndAwardJobCompletionBadges(application.getWorker());
        }

        return ResponseEntity.ok().build();
    }
}
