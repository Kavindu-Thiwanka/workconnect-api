package com.workconnect.api.controller;

import com.workconnect.api.dto.CreateJobRequestDto;
import com.workconnect.api.dto.UpdateJobRequestDto;
import com.workconnect.api.dto.UpdateJobStatusDto;
import com.workconnect.api.dto.JobDetailDto;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.dto.ApplicationStatusResponse;
import com.workconnect.api.entity.JobImage;
import com.workconnect.api.entity.JobPosting;
import com.workconnect.api.service.FileUploadService;
import com.workconnect.api.service.JobService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;
    private final FileUploadService fileUploadService;

    public JobController(JobService jobService, FileUploadService fileUploadService) {
        this.jobService = jobService;
        this.fileUploadService = fileUploadService;
    }

    @PostMapping
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobPosting> postJob(@Valid @RequestBody CreateJobRequestDto jobDto, Principal principal) {
        JobPosting createdJob = jobService.createJob(principal.getName(), jobDto);
        return new ResponseEntity<>(createdJob, HttpStatus.CREATED);
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobPosting> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody UpdateJobRequestDto jobDto,
            Principal principal) {
        JobPosting updatedJob = jobService.updateJob(principal.getName(), jobId, jobDto);
        return ResponseEntity.ok(updatedJob);
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId, Principal principal) {
        jobService.deleteJob(principal.getName(), jobId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{jobId}/status")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobPosting> updateJobStatus(
            @PathVariable Long jobId,
            @Valid @RequestBody UpdateJobStatusDto statusDto,
            Principal principal) {
        JobPosting updatedJob = jobService.updateJobStatus(principal.getName(), jobId, statusDto.getStatus());
        return ResponseEntity.ok(updatedJob);
    }

    @GetMapping
    public ResponseEntity<List<JobListingDto>> getOpenJobs() {
        List<JobListingDto> jobs = jobService.getAllOpenJobs();
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/search")
    public ResponseEntity<List<JobListingDto>> searchJobs(@RequestParam String keyword) {
        List<JobListingDto> jobs = jobService.searchOpenJobs(keyword);
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobDetailDto> getJobDetails(@PathVariable Long jobId) {
        JobDetailDto jobDto = jobService.getJobById(jobId);
        return ResponseEntity.ok(jobDto);
    }

    @GetMapping("/{jobId}/application-status")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<ApplicationStatusResponse> checkApplicationStatus(@PathVariable Long jobId, Principal principal) {
        ApplicationStatusResponse response = jobService.checkApplicationStatus(principal.getName(), jobId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{jobId}/apply")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<String> applyForJob(@PathVariable Long jobId, Principal principal) {
        jobService.applyForJob(principal.getName(), jobId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Application submitted successfully.");
    }

    @PostMapping("/{jobId}/images")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<JobImage> addJobImage(
            @PathVariable Long jobId,
            @RequestParam("file") MultipartFile file,
            Principal principal) {
        try {
            String imageUrl = fileUploadService.uploadFile(file);
            JobImage savedImage = jobService.addImageToJob(principal.getName(), jobId, imageUrl);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedImage);
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload image", e);
        }
    }
}
