package com.workconnect.api.controller;

import com.workconnect.api.dto.JobApplicationDto;
import com.workconnect.api.dto.UpdateApplicationStatusDto;
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

    public EmployerController(JobService jobService) {
        this.jobService = jobService;
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
        jobService.updateApplicationStatus(principal.getName(), applicationId, statusDto.getStatus());
        return ResponseEntity.ok().build();
    }
}
