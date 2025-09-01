package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.*;
import com.workconnect.api.dto.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.*;
import com.workconnect.api.service.AdminService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Transactional
public class AdminServiceImpl implements AdminService {

    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final ReviewRepository reviewRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminServiceImpl(UserRepository userRepository,
                           JobPostingRepository jobPostingRepository,
                           JobApplicationRepository jobApplicationRepository,
                           ReviewRepository reviewRepository,
                           WorkerProfileRepository workerProfileRepository,
                           EmployerProfileRepository employerProfileRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.reviewRepository = reviewRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.employerProfileRepository = employerProfileRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public AdminStatsDto getSystemStatistics() {
        AdminStatsDto stats = new AdminStatsDto();
        
        // User statistics
        stats.setTotalUsers(userRepository.count());
        stats.setTotalWorkers(userRepository.countByRole(Role.WORKER));
        stats.setTotalEmployers(userRepository.countByRole(Role.EMPLOYER));
        stats.setTotalAdmins(userRepository.countByRole(Role.ADMIN));
        stats.setActiveUsers(userRepository.countByStatus(UserStatus.ACTIVE));
        stats.setInactiveUsers(userRepository.countByStatus(UserStatus.INACTIVE));
        stats.setBannedUsers(userRepository.countByStatus(UserStatus.BANNED));
        
        // Job statistics
        stats.setTotalJobs(jobPostingRepository.count());
        stats.setOpenJobs(jobPostingRepository.countByStatus(JobStatus.OPEN));
        stats.setClosedJobs(jobPostingRepository.countByStatus(JobStatus.CLOSED));
        stats.setFilledJobs(jobPostingRepository.countByStatus(JobStatus.FILLED));
        stats.setExpiredJobs(jobPostingRepository.countByStatus(JobStatus.EXPIRED));
        
        // Application statistics
        stats.setTotalApplications(jobApplicationRepository.count());
        stats.setPendingApplications(jobApplicationRepository.countByStatus(JobApplicationStatus.PENDING));
        stats.setApprovedApplications(jobApplicationRepository.countByStatus(JobApplicationStatus.ACCEPTED));
        stats.setRejectedApplications(jobApplicationRepository.countByStatus(JobApplicationStatus.REJECTED));
        
        // Recent activity (approximation using ID-based queries)
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        stats.setNewUsersThisWeek(userRepository.countByCreatedAtAfter(oneWeekAgo));
        stats.setNewJobsThisWeek(jobPostingRepository.countByCreatedAtAfter(oneWeekAgo));
        stats.setNewApplicationsThisWeek(jobApplicationRepository.countByCreatedAtAfter(oneWeekAgo));
        
        // Calculate averages
        if (stats.getTotalEmployers() > 0) {
            stats.setAverageJobsPerEmployer((double) stats.getTotalJobs() / stats.getTotalEmployers());
        }
        if (stats.getTotalJobs() > 0) {
            stats.setAverageApplicationsPerJob((double) stats.getTotalApplications() / stats.getTotalJobs());
        }
        if (stats.getTotalWorkers() > 0) {
            stats.setAverageApplicationsPerWorker((double) stats.getTotalApplications() / stats.getTotalWorkers());
        }
        
        return stats;
    }

    @Override
    public Page<AdminUserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::convertToAdminUserDto);
    }

    @Override
    public Page<AdminUserDto> searchUsers(String searchTerm, Pageable pageable) {
        return userRepository.findByEmailContainingIgnoreCase(searchTerm, pageable)
                .map(this::convertToAdminUserDto);
    }

    @Override
    public AdminUserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return convertToAdminUserDto(user);
    }

    @Override
    public void updateUserStatus(Long userId, UserStatus status) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(status);
        userRepository.save(user);
    }

    @Override
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    public Page<AdminJobDto> getAllJobs(Pageable pageable) {
        return jobPostingRepository.findAll(pageable).map(this::convertToAdminJobDto);
    }

    @Override
    public Page<AdminJobDto> searchJobs(String searchTerm, Pageable pageable) {
        return jobPostingRepository.findByJobTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                searchTerm, searchTerm, pageable).map(this::convertToAdminJobDto);
    }

    @Override
    public AdminJobDto getJobById(Long jobId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));
        return convertToAdminJobDto(job);
    }

    @Override
    public void deleteJob(Long jobId) {
        if (!jobPostingRepository.existsById(jobId)) {
            throw new RuntimeException("Job not found");
        }
        jobPostingRepository.deleteById(jobId);
    }

    @Override
    public Page<AdminApplicationDto> getAllApplications(Pageable pageable) {
        return jobApplicationRepository.findAll(pageable).map(this::convertToAdminApplicationDto);
    }

    @Override
    public Page<AdminApplicationDto> getApplicationsByJob(Long jobId, Pageable pageable) {
        return jobApplicationRepository.findByJobPosting_Id(jobId, pageable)
                .map(this::convertToAdminApplicationDto);
    }

    @Override
    public Page<AdminApplicationDto> getApplicationsByWorker(Long workerId, Pageable pageable) {
        return jobApplicationRepository.findByWorker_UserId(workerId, pageable)
                .map(this::convertToAdminApplicationDto);
    }

    @Override
    public AdminApplicationDto getApplicationById(Long applicationId) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        return convertToAdminApplicationDto(application);
    }

    @Override
    public Page<ReviewDto> getAllReviews(Pageable pageable) {
        return reviewRepository.findAll(pageable).map(this::convertToReviewDto);
    }

    @Override
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new RuntimeException("Review not found");
        }
        reviewRepository.deleteById(reviewId);
    }

    @Override
    public byte[] exportUsersToCSV() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("ID,Email,Role,Status,Display Name,Location,Company Name,Total Applications,Total Job Postings");
            
            // CSV Data
            userRepository.findAll().forEach(user -> {
                AdminUserDto dto = convertToAdminUserDto(user);
                writer.printf("%d,%s,%s,%s,%s,%s,%s,%d,%d%n",
                    dto.getUserId(),
                    dto.getEmail(),
                    dto.getRole(),
                    dto.getStatus(),
                    dto.getDisplayName() != null ? dto.getDisplayName() : "",
                    dto.getLocation() != null ? dto.getLocation() : "",
                    dto.getCompanyName() != null ? dto.getCompanyName() : "",
                    dto.getTotalApplications(),
                    dto.getTotalJobPostings()
                );
            });
            
            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export users to CSV", e);
        }
    }

    @Override
    public byte[] exportJobsToCSV() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("ID,Title,Description,Location,Salary,Job Type,Status,Employer,Company,Total Applications");
            
            // CSV Data
            jobPostingRepository.findAll().forEach(job -> {
                AdminJobDto dto = convertToAdminJobDto(job);
                writer.printf("%d,%s,%s,%s,%.2f,%s,%s,%s,%s,%d%n",
                    dto.getId(),
                    dto.getJobTitle(),
                    dto.getDescription().replace(",", ";"), // Replace commas to avoid CSV issues
                    dto.getLocation(),
                    dto.getSalary() != null ? dto.getSalary() : 0.0,
                    dto.getJobType(),
                    dto.getStatus(),
                    dto.getEmployerEmail(),
                    dto.getEmployerCompanyName(),
                    dto.getTotalApplications()
                );
            });
            
            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export jobs to CSV", e);
        }
    }

    @Override
    public byte[] exportApplicationsToCSV() {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             PrintWriter writer = new PrintWriter(baos)) {
            
            // CSV Header
            writer.println("ID,Job Title,Worker Name,Worker Email,Status,Applied At,Status Updated At");
            
            // CSV Data
            jobApplicationRepository.findAll().forEach(application -> {
                AdminApplicationDto dto = convertToAdminApplicationDto(application);
                writer.printf("%d,%s,%s,%s,%s,%s,%s%n",
                    dto.getId(),
                    dto.getJobTitle(),
                    dto.getWorkerName(),
                    dto.getWorkerEmail(),
                    dto.getStatus(),
                    dto.getAppliedAt() != null ? dto.getAppliedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : "",
                    dto.getStatusUpdatedAt() != null ? dto.getStatusUpdatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) : ""
                );
            });
            
            writer.flush();
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to export applications to CSV", e);
        }
    }

    // Conversion methods
    private AdminUserDto convertToAdminUserDto(User user) {
        AdminUserDto dto = new AdminUserDto();
        dto.setUserId(user.getUserId());
        dto.setEmail(user.getEmail());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus());

        // Set profile-specific information
        if (user.getProfile() instanceof WorkerProfile) {
            WorkerProfile workerProfile = (WorkerProfile) user.getProfile();
            dto.setProfileType("WORKER");
            dto.setDisplayName(workerProfile.getFirstName() + " " + workerProfile.getLastName());
            dto.setLocation(workerProfile.getLocation());
            dto.setPhoneNumber(workerProfile.getPhoneNumber());
            dto.setTotalApplications(Math.toIntExact(jobApplicationRepository.countByWorker_UserId(user.getUserId())));
        } else if (user.getProfile() instanceof EmployerProfile) {
            EmployerProfile employerProfile = (EmployerProfile) user.getProfile();
            dto.setProfileType("EMPLOYER");
            dto.setDisplayName(employerProfile.getCompanyName());
            dto.setCompanyName(employerProfile.getCompanyName());
            dto.setLocation(employerProfile.getLocation());
            dto.setTotalJobPostings(Math.toIntExact(jobPostingRepository.countByEmployer_UserId(user.getUserId())));
        } else {
            dto.setProfileType("ADMIN");
            dto.setDisplayName(user.getEmail());
        }

        // Note: createdAt and lastLoginAt fields don't exist in User entity
        // These would need to be added to the User entity for full functionality

        return dto;
    }

    private AdminJobDto convertToAdminJobDto(JobPosting job) {
        AdminJobDto dto = new AdminJobDto();
        dto.setId(job.getId());
        dto.setJobTitle(job.getJobTitle());
        dto.setDescription(job.getDescription());
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());
        dto.setJobType(job.getJobType());
        dto.setStatus(job.getStatus());
        dto.setStartDate(job.getStartDate());
        dto.setEndDate(job.getEndDate());
        dto.setCreatedAt(job.getPostedAt());
        dto.setEmployerEmail(job.getEmployer().getEmail());

        // Get employer company name
        if (job.getEmployer().getProfile() instanceof EmployerProfile) {
            EmployerProfile employerProfile = (EmployerProfile) job.getEmployer().getProfile();
            dto.setEmployerCompanyName(employerProfile.getCompanyName());
        }

        // Calculate application statistics
        dto.setTotalApplications(Math.toIntExact(jobApplicationRepository.countByJobPosting_Id(job.getId())));
        dto.setApprovedApplications(Math.toIntExact(jobApplicationRepository.countByJobPosting_IdAndStatus(
                job.getId(), JobApplicationStatus.ACCEPTED)));
        dto.setRejectedApplications(Math.toIntExact(jobApplicationRepository.countByJobPosting_IdAndStatus(
                job.getId(), JobApplicationStatus.REJECTED)));
        dto.setPendingApplications(Math.toIntExact(jobApplicationRepository.countByJobPosting_IdAndStatus(
                job.getId(), JobApplicationStatus.PENDING)));

        return dto;
    }

    private AdminApplicationDto convertToAdminApplicationDto(JobApplication application) {
        AdminApplicationDto dto = new AdminApplicationDto();
        dto.setId(application.getId());
        dto.setJobId(application.getJobPosting().getId());
        dto.setJobTitle(application.getJobPosting().getJobTitle());
        dto.setEmployerEmail(application.getJobPosting().getEmployer().getEmail());
        dto.setWorkerId(application.getWorker().getUserId());
        dto.setWorkerEmail(application.getWorker().getEmail());
        dto.setStatus(application.getStatus());
        dto.setAppliedAt(application.getAppliedAt());
        dto.setStatusUpdatedAt(application.getStatusUpdatedAt());
        dto.setCoverLetter(application.getCoverLetter());

        // Get employer company name
        if (application.getJobPosting().getEmployer().getProfile() instanceof EmployerProfile) {
            EmployerProfile employerProfile = (EmployerProfile) application.getJobPosting().getEmployer().getProfile();
            dto.setEmployerCompanyName(employerProfile.getCompanyName());
        }

        // Get worker name
        if (application.getWorker().getProfile() instanceof WorkerProfile) {
            WorkerProfile workerProfile = (WorkerProfile) application.getWorker().getProfile();
            dto.setWorkerName(workerProfile.getFirstName() + " " + workerProfile.getLastName());
        }

        return dto;
    }

    private ReviewDto convertToReviewDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());

        // Set reviewer name based on profile type
        if (review.getReviewer().getProfile() instanceof WorkerProfile) {
            WorkerProfile workerProfile = (WorkerProfile) review.getReviewer().getProfile();
            dto.setReviewerName(workerProfile.getFirstName() + " " + workerProfile.getLastName());
        } else if (review.getReviewer().getProfile() instanceof EmployerProfile) {
            EmployerProfile employerProfile = (EmployerProfile) review.getReviewer().getProfile();
            dto.setReviewerName(employerProfile.getCompanyName());
        } else {
            dto.setReviewerName(review.getReviewer().getEmail());
        }

        return dto;
    }

    @Override
    public void createAdminUser(String email, String password) {
        // Check if user already exists
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("User with email " + email + " already exists");
        }

        // Create admin user
        User adminUser = new User();
        adminUser.setEmail(email);
        adminUser.setPassword(passwordEncoder.encode(password));
        adminUser.setRole(Role.ADMIN);
        adminUser.setStatus(UserStatus.ACTIVE);
        // Admin users don't need a profile

        userRepository.save(adminUser);
    }
}
