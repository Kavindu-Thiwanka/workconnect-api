package com.workconnect.api.controller;

import com.workconnect.api.constants.Enum.UserStatus;
import com.workconnect.api.dto.*;
import com.workconnect.api.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Statistics endpoint
    @GetMapping("/stats")
    public ResponseEntity<AdminStatsDto> getSystemStatistics() {
        AdminStatsDto stats = adminService.getSystemStatistics();
        return ResponseEntity.ok(stats);
    }

    // User management endpoints
    @GetMapping("/users")
    public ResponseEntity<Page<AdminUserDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "userId") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AdminUserDto> users = search != null && !search.trim().isEmpty() ?
            adminService.searchUsers(search, pageable) :
            adminService.getAllUsers(pageable);
            
        return ResponseEntity.ok(users);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<AdminUserDto> getUserById(@PathVariable Long userId) {
        AdminUserDto user = adminService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/users/{userId}/status")
    public ResponseEntity<String> updateUserStatus(
            @PathVariable Long userId,
            @RequestBody UserStatusUpdateDto statusUpdate) {
        adminService.updateUserStatus(userId, statusUpdate.getStatus());
        return ResponseEntity.ok("User status updated successfully");
    }

    @DeleteMapping("/users/{userId}")
    public ResponseEntity<String> deleteUser(@PathVariable Long userId) {
        adminService.deleteUser(userId);
        return ResponseEntity.ok("User deleted successfully");
    }

    // Job management endpoints
    @GetMapping("/jobs")
    public ResponseEntity<Page<AdminJobDto>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(required = false) String search) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AdminJobDto> jobs = search != null && !search.trim().isEmpty() ?
            adminService.searchJobs(search, pageable) :
            adminService.getAllJobs(pageable);
            
        return ResponseEntity.ok(jobs);
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<AdminJobDto> getJobById(@PathVariable Long jobId) {
        AdminJobDto job = adminService.getJobById(jobId);
        return ResponseEntity.ok(job);
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<String> deleteJob(@PathVariable Long jobId) {
        adminService.deleteJob(jobId);
        return ResponseEntity.ok("Job deleted successfully");
    }

    // Application management endpoints
    @GetMapping("/applications")
    public ResponseEntity<Page<AdminApplicationDto>> getAllApplications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<AdminApplicationDto> applications = adminService.getAllApplications(pageable);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/job/{jobId}")
    public ResponseEntity<Page<AdminApplicationDto>> getApplicationsByJob(
            @PathVariable Long jobId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<AdminApplicationDto> applications = adminService.getApplicationsByJob(jobId, pageable);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/worker/{workerId}")
    public ResponseEntity<Page<AdminApplicationDto>> getApplicationsByWorker(
            @PathVariable Long workerId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("appliedAt").descending());
        Page<AdminApplicationDto> applications = adminService.getApplicationsByWorker(workerId, pageable);
        return ResponseEntity.ok(applications);
    }

    @GetMapping("/applications/{applicationId}")
    public ResponseEntity<AdminApplicationDto> getApplicationById(@PathVariable Long applicationId) {
        AdminApplicationDto application = adminService.getApplicationById(applicationId);
        return ResponseEntity.ok(application);
    }

    // Review management endpoints
    @GetMapping("/reviews")
    public ResponseEntity<Page<ReviewDto>> getAllReviews(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        Sort sort = sortDir.equalsIgnoreCase("desc") ? 
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        
        Page<ReviewDto> reviews = adminService.getAllReviews(pageable);
        return ResponseEntity.ok(reviews);
    }

    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<String> deleteReview(@PathVariable Long reviewId) {
        adminService.deleteReview(reviewId);
        return ResponseEntity.ok("Review deleted successfully");
    }

    // Data export endpoints
    @GetMapping("/export/users")
    public ResponseEntity<byte[]> exportUsers() {
        byte[] csvData = adminService.exportUsersToCSV();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "users_export.csv");
        
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    @GetMapping("/export/jobs")
    public ResponseEntity<byte[]> exportJobs() {
        byte[] csvData = adminService.exportJobsToCSV();
        
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "jobs_export.csv");
        
        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    @GetMapping("/export/applications")
    public ResponseEntity<byte[]> exportApplications() {
        byte[] csvData = adminService.exportApplicationsToCSV();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "applications_export.csv");

        return new ResponseEntity<>(csvData, headers, HttpStatus.OK);
    }

    // Admin user creation endpoint (for initial setup)
    @PostMapping("/create-admin")
    public ResponseEntity<String> createAdminUser(@RequestBody CreateAdminRequest request) {
        adminService.createAdminUser(request.getEmail(), request.getPassword());
        return ResponseEntity.ok("Admin user created successfully");
    }
}

// DTO for user status updates
class UserStatusUpdateDto {
    private UserStatus status;

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }
}

// DTO for admin user creation
class CreateAdminRequest {
    private String email;
    private String password;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
