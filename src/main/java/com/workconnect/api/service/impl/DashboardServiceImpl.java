package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.dto.JobApplicationDto;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.dto.dashboard.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.*;
import com.workconnect.api.service.DashboardService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;
import java.util.ArrayList;

@Service
public class DashboardServiceImpl implements DashboardService {

    private final UserRepository userRepository;
    private final WorkerProfileRepository workerProfileRepository;
    private final EmployerProfileRepository employerProfileRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public DashboardServiceImpl(
            UserRepository userRepository,
            WorkerProfileRepository workerProfileRepository,
            EmployerProfileRepository employerProfileRepository,
            JobPostingRepository jobPostingRepository,
            JobApplicationRepository jobApplicationRepository) {
        this.userRepository = userRepository;
        this.workerProfileRepository = workerProfileRepository;
        this.employerProfileRepository = employerProfileRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobApplicationRepository = jobApplicationRepository;
    }

    @Override
    public WorkerDashboardDto getWorkerDashboard(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify user is a worker
        WorkerProfile workerProfile = workerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Worker profile not found"));

        // Get statistics
        WorkerStatsDto stats = getWorkerStats(userEmail);

        // Get profile completion
        ProfileCompletionDto completion = getWorkerProfileCompletion(userEmail);

        // Get recent applications
        RecentApplicationsDto recentApps = getWorkerRecentApplications(userEmail, 5);

        // Get recommendations
        JobRecommendationsDto recommendations = getWorkerRecommendations(userEmail, 6);

        return WorkerDashboardDto.builder()
                .totalApplications(stats.getTotalApplications())
                .pendingApplications(stats.getPendingApplications())
                .interviewsScheduled(stats.getInterviewsScheduled())
                .profileViews(stats.getProfileViews())
                .profileCompletionPercentage(completion.getPercentage())
                .recentApplications(recentApps.getApplications())
                .recommendedJobs(recommendations.getRecommendations())
                .profileCompletionTips(completion.getTips())
                .build();
    }

    @Override
    public EmployerDashboardDto getEmployerDashboard(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Verify user is an employer
        EmployerProfile employerProfile = employerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Employer profile not found"));

        // Get statistics
        EmployerStatsDto stats = getEmployerStats(userEmail);

        // Get active jobs
        ActiveJobsDto activeJobs = getEmployerActiveJobs(userEmail, 5);

        // Get recent applications
        RecentApplicationsDto recentApps = getEmployerRecentApplications(userEmail, 5);

        return EmployerDashboardDto.builder()
                .activeJobs(stats.getActiveJobs())
                .totalApplications(stats.getTotalApplications())
                .newApplicationsThisWeek(stats.getNewApplicationsThisWeek())
                .totalViews(stats.getTotalViews())
                .recentApplications(recentApps.getApplications())
                .activeJobPostings(activeJobs.getJobs())
                .build();
    }

    @Override
    public JobRecommendationsDto getWorkerRecommendations(String userEmail, int limit) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        WorkerProfile workerProfile = workerProfileRepository.findByUser(user)
                .orElseThrow(() -> new IllegalStateException("Worker profile not found"));

        // Enhanced recommendation logic based on worker profile
        List<JobPosting> openJobs = jobPostingRepository.findByStatus(JobStatus.OPEN);

        // Filter jobs based on worker location if available
        List<JobPosting> filteredJobs = openJobs.stream()
                .filter(job -> workerProfile.getLocation() == null ||
                              job.getLocation() == null ||
                              job.getLocation().toLowerCase().contains(workerProfile.getLocation().toLowerCase()))
                .limit(limit)
                .collect(Collectors.toList());

        List<JobListingDto> jobDtos = filteredJobs.stream()
                .map(this::convertToJobListingDto)
                .collect(Collectors.toList());

        return JobRecommendationsDto.builder()
                .recommendations(jobDtos)
                .totalCount(jobDtos.size())
                .recommendationReason("Based on your location and profile")
                .build();
    }

    @Override
    public RecentApplicationsDto getWorkerRecentApplications(String userEmail, int limit) {
        List<JobApplication> applications = jobApplicationRepository.findByWorker_Email(userEmail)
                .stream()
                .sorted((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()))
                .limit(limit)
                .collect(Collectors.toList());

        List<JobApplicationDto> applicationDtos = applications.stream()
                .map(this::convertToJobApplicationDto)
                .collect(Collectors.toList());

        return RecentApplicationsDto.builder()
                .applications(applicationDtos)
                .totalCount(applicationDtos.size())
                .build();
    }

    @Override
    public ActiveJobsDto getEmployerActiveJobs(String userEmail, int limit) {
        List<JobPosting> activeJobs = jobPostingRepository.findByEmployer_Email(userEmail)
                .stream()
                .filter(job -> job.getStatus() == JobStatus.OPEN)
                .sorted((a, b) -> b.getPostedAt().compareTo(a.getPostedAt()))
                .limit(limit)
                .collect(Collectors.toList());

        List<JobListingDto> jobDtos = activeJobs.stream()
                .map(this::convertToJobListingDto)
                .collect(Collectors.toList());

        return ActiveJobsDto.builder()
                .jobs(jobDtos)
                .totalCount(jobDtos.size())
                .build();
    }

    @Override
    public RecentApplicationsDto getEmployerRecentApplications(String userEmail, int limit) {
        // Get applications for employer's jobs
        List<JobPosting> employerJobs = jobPostingRepository.findByEmployer_Email(userEmail);
        
        List<JobApplication> applications = new ArrayList<>();
        for (JobPosting job : employerJobs) {
            applications.addAll(jobApplicationRepository.findByJobPosting_Id(job.getId()));
        }

        List<JobApplicationDto> applicationDtos = applications.stream()
                .sorted((a, b) -> b.getAppliedAt().compareTo(a.getAppliedAt()))
                .limit(limit)
                .map(this::convertToJobApplicationDto)
                .collect(Collectors.toList());

        return RecentApplicationsDto.builder()
                .applications(applicationDtos)
                .totalCount(applicationDtos.size())
                .build();
    }

    @Override
    public ProfileCompletionDto getWorkerProfileCompletion(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get worker profile
        WorkerProfile workerProfile = workerProfileRepository.findByUser(user).orElse(null);

        List<String> missingFields = new ArrayList<>();
        List<String> tips = new ArrayList<>();
        int totalFields = 10; // Total number of profile fields
        int completedFields = 0;

        if (workerProfile != null) {
            // Check first name
            if (workerProfile.getFirstName() != null && !workerProfile.getFirstName().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("firstName");
                tips.add("Add your first name");
            }

            // Check last name
            if (workerProfile.getLastName() != null && !workerProfile.getLastName().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("lastName");
                tips.add("Add your last name");
            }

            // Check phone number
            if (workerProfile.getPhoneNumber() != null && !workerProfile.getPhoneNumber().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("phoneNumber");
                tips.add("Add your phone number");
            }

            // Check location
            if (workerProfile.getLocation() != null && !workerProfile.getLocation().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("location");
                tips.add("Add your location");
            }

            // Check bio
            if (workerProfile.getBio() != null && !workerProfile.getBio().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("bio");
                tips.add("Write a professional bio");
            }

            // Check skills
            if (workerProfile.getSkills() != null && !workerProfile.getSkills().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("skills");
                tips.add("Add your skills");
            }

            // Check experience
            if (workerProfile.getExperience() != null && !workerProfile.getExperience().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("experience");
                tips.add("Add your work experience");
            }

            // Check education
            if (workerProfile.getEducation() != null && !workerProfile.getEducation().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("education");
                tips.add("Add your education background");
            }

            // Check profile picture
            if (workerProfile.getProfileImageUrl() != null && !workerProfile.getProfileImageUrl().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("profilePicture");
                tips.add("Upload a professional profile picture");
            }

            // Check resume
            if (workerProfile.getResumeUrl() != null && !workerProfile.getResumeUrl().isEmpty()) {
                completedFields++;
            } else {
                missingFields.add("resume");
                tips.add("Upload your resume");
            }
        } else {
            // No profile exists, all fields are missing
            missingFields.addAll(List.of("firstName", "lastName", "phoneNumber", "location", "bio",
                                       "skills", "experience", "education", "profilePicture", "resume"));
            tips.addAll(List.of("Add your first name", "Add your last name", "Add your phone number",
                               "Add your location", "Write a professional bio", "Add your skills",
                               "Add your work experience", "Add your education background",
                               "Upload a professional profile picture", "Upload your resume"));
        }

        double percentage = ((double) completedFields / totalFields) * 100;

        return ProfileCompletionDto.builder()
                .percentage(percentage)
                .missingFields(missingFields)
                .tips(tips)
                .build();
    }

    @Override
    public WorkerStatsDto getWorkerStats(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get total applications
        List<JobApplication> allApplications = jobApplicationRepository.findByWorker_Email(userEmail);
        int totalApplications = allApplications.size();

        // Get pending applications
        long pendingApplications = jobApplicationRepository.countByWorkerAndStatus(user, JobApplicationStatus.PENDING);

        // Get interviews scheduled (applications with INTERVIEWED status)
        long interviewsScheduled = jobApplicationRepository.countByWorkerAndStatus(user, JobApplicationStatus.COMPLETED);

        // Profile views (simplified - could be tracked separately)
        int profileViews = totalApplications * 3 + 15; // Placeholder logic

        // Job matches this week (simplified)
        int jobMatchesThisWeek = (int) jobPostingRepository.findByStatus(JobStatus.OPEN).size();

        return WorkerStatsDto.builder()
                .totalApplications(totalApplications)
                .pendingApplications((int) pendingApplications)
                .interviewsScheduled((int) interviewsScheduled)
                .profileViews(profileViews)
                .jobMatchesThisWeek(jobMatchesThisWeek)
                .averageResponseTime(3.5) // Placeholder
                .build();
    }

    @Override
    public EmployerStatsDto getEmployerStats(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Get all employer's jobs
        List<JobPosting> employerJobs = jobPostingRepository.findByEmployer_Email(userEmail);

        // Get active jobs
        int activeJobs = (int) employerJobs.stream()
                .filter(job -> job.getStatus() == JobStatus.OPEN)
                .count();

        // Get total jobs posted
        int totalJobsPosted = employerJobs.size();

        // Get total applications for employer's jobs
        int totalApplications = 0;
        int newApplicationsThisWeek = 0;
        LocalDateTime oneWeekAgo = LocalDateTime.now().minus(7, ChronoUnit.DAYS);

        for (JobPosting job : employerJobs) {
            List<JobApplication> jobApplications = jobApplicationRepository.findByJobPosting_Id(job.getId());
            totalApplications += jobApplications.size();

            // Count new applications this week
            newApplicationsThisWeek += (int) jobApplications.stream()
                    .filter(app -> app.getAppliedAt().isAfter(oneWeekAgo))
                    .count();
        }

        // Total views (simplified - could be tracked separately)
        int totalViews = totalJobsPosted * 25 + totalApplications * 3; // Placeholder logic

        // Average applications per job
        double averageApplicationsPerJob = totalJobsPosted > 0 ?
                (double) totalApplications / totalJobsPosted : 0.0;

        return EmployerStatsDto.builder()
                .activeJobs(activeJobs)
                .totalApplications(totalApplications)
                .newApplicationsThisWeek(newApplicationsThisWeek)
                .totalViews(totalViews)
                .totalJobsPosted(totalJobsPosted)
                .averageApplicationsPerJob(averageApplicationsPerJob)
                .build();
    }

    // Helper methods for DTO conversion
    private JobListingDto convertToJobListingDto(JobPosting jobPosting) {
        // Get employer company name from EmployerProfile
        String employerCompanyName = "Company";
        if (jobPosting.getEmployer().getProfile() instanceof EmployerProfile) {
            EmployerProfile employerProfile = (EmployerProfile) jobPosting.getEmployer().getProfile();
            employerCompanyName = employerProfile.getCompanyName() != null ?
                    employerProfile.getCompanyName() : "Company";
        }

        return JobListingDto.builder()
                .id(jobPosting.getId())
                .jobTitle(jobPosting.getJobTitle())
                .description(jobPosting.getDescription())
                .location(jobPosting.getLocation())
                .jobType(jobPosting.getJobType())
                .salary(jobPosting.getSalary())
                .requiredSkills(jobPosting.getRequiredSkills())
                .status(jobPosting.getStatus())
                .employerCompanyName(employerCompanyName)
                .postedAt(jobPosting.getPostedAt())
                .build();
    }

    private JobApplicationDto convertToJobApplicationDto(JobApplication application) {
        // Get employer company name from EmployerProfile
        String employerCompanyName = "Company";
        if (application.getJobPosting().getEmployer().getProfile() instanceof EmployerProfile) {
            EmployerProfile employerProfile = (EmployerProfile) application.getJobPosting().getEmployer().getProfile();
            employerCompanyName = employerProfile.getCompanyName() != null ?
                    employerProfile.getCompanyName() : "Company";
        }

        return JobApplicationDto.builder()
                .id(application.getId())
                .jobId(application.getJobPosting().getId())
                .jobTitle(application.getJobPosting().getJobTitle())
                .employerCompanyName(employerCompanyName)
                .status(application.getStatus())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
