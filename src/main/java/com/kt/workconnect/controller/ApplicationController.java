package com.kt.workconnect.controller;

import com.kt.workconnect.entity.JobApplication;
import com.kt.workconnect.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;

    @PostMapping
    public ResponseEntity<?> applyForJob(@RequestParam UUID jobId, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        try {
            JobApplication application = applicationService.applyForJob(jobId, email);
            return ResponseEntity.ok(application);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/job/{jobId}")
    public ResponseEntity<?> getApplicationsForJob(@PathVariable UUID jobId, @AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        try {
            List<JobApplication> applications = applicationService.getApplicationsForJob(jobId, email);
            return ResponseEntity.ok(applications);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/jobs")
    public ResponseEntity<?> getAllJobApplications(@AuthenticationPrincipal Jwt jwt) {
        String email = jwt.getClaimAsString("email");
        try {
            List<JobApplication> applications = applicationService.getAllJobApplications(email);
            return ResponseEntity.ok(applications);
        } catch (AccessDeniedException e) {
            return ResponseEntity.status(403).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
