package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.constants.Enum.JobType;
import com.workconnect.api.dto.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.JobApplicationRepository;
import com.workconnect.api.repository.JobImageRepository;
import com.workconnect.api.repository.JobPostingRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.JobService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;
    private final JobImageRepository jobImageRepository;

    public JobServiceImpl(JobPostingRepository jobPostingRepository, UserRepository userRepository, JobApplicationRepository jobApplicationRepository,
                          JobImageRepository jobImageRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
        this.jobApplicationRepository = jobApplicationRepository;
        this.jobImageRepository = jobImageRepository;
    }

    @Override
    public JobPosting createJob(String employerEmail, CreateJobRequestDto jobDto) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Employer not found"));

        JobPosting newJob = new JobPosting();
        newJob.setJobTitle(jobDto.getJobTitle());
        newJob.setDescription(jobDto.getDescription());
        newJob.setJobType(jobDto.getJobType());
        newJob.setEmployer(employer);
        newJob.setStatus(JobStatus.OPEN);

        if (jobDto.getJobType() == JobType.ONE_DAY) {
            if (jobDto.getStartDate() == null) {
                throw new IllegalArgumentException("A start date (jobDate) is required for a one-day job.");
            }
            newJob.setStartDate(jobDto.getStartDate());
            newJob.setEndDate(null);
        } else if (jobDto.getJobType() == JobType.CONTRACT) {
            if (jobDto.getStartDate() == null || jobDto.getEndDate() == null) {
                throw new IllegalArgumentException("Both a start date and an end date are required for a contract job.");
            }
            newJob.setStartDate(jobDto.getStartDate());
            newJob.setEndDate(jobDto.getEndDate());
        }

        return jobPostingRepository.save(newJob);
    }

    @Override
    public List<JobListingDto> getAllOpenJobs() {
        List<JobPosting> openJobs = jobPostingRepository.findByStatus(JobStatus.OPEN);
        return openJobs.stream()
                .map(this::mapToJobListingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<JobListingDto> searchOpenJobs(String keyword) {
        List<JobPosting> foundJobs = jobPostingRepository
                .findByStatusAndJobTitleContainingIgnoreCaseOrDescriptionContainingIgnoreCase(
                        JobStatus.OPEN, keyword, keyword
                );
        return foundJobs.stream()
                .map(this::mapToJobListingDto)
                .collect(Collectors.toList());
    }

    @Override
    public JobListingDto mapToJobListingDto(JobPosting job) {
        JobListingDto dto = new JobListingDto();
        dto.setId(job.getId());
        dto.setJobTitle(job.getJobTitle());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());
        dto.setJobType(job.getJobType());
        dto.setPostedAt(job.getPostedAt());

        if (job.getEmployer().getProfile() instanceof EmployerProfile) {
            EmployerProfile employerProfile = (EmployerProfile) job.getEmployer().getProfile();
            dto.setEmployerCompanyName(employerProfile.getCompanyName());
        }

        return dto;
    }

    @Override
    public JobDetailDto getJobById(Long jobId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found with id: " + jobId));

        return mapToJobDetailDto(job);
    }

    private JobDetailDto mapToJobDetailDto(JobPosting job) {
        JobDetailDto dto = new JobDetailDto();
        dto.setId(job.getId());
        dto.setJobTitle(job.getJobTitle());
        dto.setDescription(job.getDescription());
        dto.setRequiredSkills(job.getRequiredSkills());
        dto.setLocation(job.getLocation());
        dto.setSalary(job.getSalary());
        dto.setJobType(job.getJobType());
        dto.setPostedAt(job.getPostedAt());

        if (job.getEmployer().getProfile() instanceof EmployerProfile) {
            dto.setEmployerCompanyName(((EmployerProfile) job.getEmployer().getProfile()).getCompanyName());
        }

        if (job.getJobType() == JobType.ONE_DAY) {
            dto.setJobDate(job.getStartDate());
        } else if (job.getJobType() == JobType.CONTRACT) {
            dto.setStartDate(job.getStartDate());
            dto.setEndDate(job.getEndDate());
        }

        return dto;
    }

    @Transactional
    @Override
    public void applyForJob(String workerEmail, Long jobId) {
        User worker = userRepository.findByEmail(workerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Worker not found"));

        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (job.getStatus() != JobStatus.OPEN) {
            throw new IllegalStateException("This job is no longer open for applications.");
        }
        if (jobApplicationRepository.existsByWorker_UserIdAndJobPosting_Id(worker.getUserId(), jobId)) {
            throw new IllegalStateException("You have already applied for this job.");
        }

        JobApplication application = new JobApplication();
        application.setWorker(worker);
        application.setJobPosting(job);
        application.setStatus(JobApplicationStatus.PENDING);

        jobApplicationRepository.save(application);
    }

    @Override
    public List<JobApplicationDto> getApplicationsForJob(String employerEmail, Long jobId) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to view applications for this job.");
        }

        List<JobApplication> applications = jobApplicationRepository.findByJobPosting_Id(jobId);
        return applications.stream()
                .map(this::mapToJobApplicationDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public JobApplication updateApplicationStatus(String employerEmail, Long applicationId, JobApplicationStatus status) {
        JobApplication application = jobApplicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (!application.getJobPosting().getEmployer().getEmail().equals(employerEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to update this application.");
        }

        application.setStatus(status);
        jobApplicationRepository.save(application);

        return application;
    }

    private JobApplicationDto mapToJobApplicationDto(JobApplication application) {
        JobApplicationDto dto = new JobApplicationDto();
        dto.setId(application.getId());
        dto.setStatus(application.getStatus());
        dto.setAppliedAt(application.getAppliedAt());

        ApplicantDto applicantDto = new ApplicantDto();
        User worker = application.getWorker();
        applicantDto.setWorkerId(worker.getUserId());

        if (worker.getProfile() instanceof WorkerProfile) {
            WorkerProfile workerProfile = (WorkerProfile) worker.getProfile();
            applicantDto.setFirstName(workerProfile.getFirstName());
            applicantDto.setLastName(workerProfile.getLastName());
            Set<String> skills = new HashSet<>();
            for (Skill skill : workerProfile.getSkills()) {
                skills.add(skill.getName());
            }
            applicantDto.setSkills(skills);
        }

        dto.setApplicant(applicantDto);
        return dto;
    }

    @Transactional
    @Override
    public JobImage addImageToJob(String employerEmail, Long jobId, String imageUrl) {
        JobPosting job = jobPostingRepository.findById(jobId)
                .orElseThrow(() -> new RuntimeException("Job not found"));

        if (!job.getEmployer().getEmail().equals(employerEmail)) {
            throw new org.springframework.security.access.AccessDeniedException("You are not authorized to add images to this job.");
        }

        JobImage jobImage = new JobImage();
        jobImage.setImageUrl(imageUrl);
        jobImage.setJobPosting(job);

        return jobImageRepository.save(jobImage);
    }

    @Override
    public List<JobListingDto> getJobsByEmployer(String employerEmail) {
        List<JobPosting> jobs = jobPostingRepository.findByEmployer_Email(employerEmail);
        return jobs.stream()
                .map(this::mapToJobListingDto)
                .collect(Collectors.toList());
    }
}
