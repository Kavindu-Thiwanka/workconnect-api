package com.workconnect.api.service;

import com.workconnect.api.constants.Enum.UserStatus;
import com.workconnect.api.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AdminService {
    
    // Statistics
    AdminStatsDto getSystemStatistics();
    
    // User Management
    Page<AdminUserDto> getAllUsers(Pageable pageable);
    Page<AdminUserDto> searchUsers(String searchTerm, Pageable pageable);
    AdminUserDto getUserById(Long userId);
    void updateUserStatus(Long userId, UserStatus status);
    void deleteUser(Long userId);
    
    // Job Management
    Page<AdminJobDto> getAllJobs(Pageable pageable);
    Page<AdminJobDto> searchJobs(String searchTerm, Pageable pageable);
    AdminJobDto getJobById(Long jobId);
    void deleteJob(Long jobId);
    
    // Application Management
    Page<AdminApplicationDto> getAllApplications(Pageable pageable);
    Page<AdminApplicationDto> getApplicationsByJob(Long jobId, Pageable pageable);
    Page<AdminApplicationDto> getApplicationsByWorker(Long workerId, Pageable pageable);
    AdminApplicationDto getApplicationById(Long applicationId);
    
    // Review Management
    Page<ReviewDto> getAllReviews(Pageable pageable);
    void deleteReview(Long reviewId);
    
    // Data Export
    byte[] exportUsersToCSV();
    byte[] exportJobsToCSV();
    byte[] exportApplicationsToCSV();

    // Admin User Management
    void createAdminUser(String email, String password);
}
