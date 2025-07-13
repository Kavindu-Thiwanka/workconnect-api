package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.constants.Enum.JobType;
import com.workconnect.api.dto.CreateJobRequestDto;
import com.workconnect.api.dto.JobDetailDto;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.entity.EmployerProfile;
import com.workconnect.api.entity.JobApplication;
import com.workconnect.api.entity.JobPosting;
import com.workconnect.api.entity.User;
import com.workconnect.api.repository.JobApplicationRepository;
import com.workconnect.api.repository.JobPostingRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.JobService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    private final JobPostingRepository jobPostingRepository;
    private final UserRepository userRepository;
    private final JobApplicationRepository jobApplicationRepository;

    public JobServiceImpl(JobPostingRepository jobPostingRepository, UserRepository userRepository, JobApplicationRepository jobApplicationRepository) {
        this.jobPostingRepository = jobPostingRepository;
        this.userRepository = userRepository;
        this.jobApplicationRepository = jobApplicationRepository;
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

    private JobListingDto mapToJobListingDto(JobPosting job) {
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
        if (jobApplicationRepository.existsByWorker_IdAndJobPosting_Id(worker.getUserId(), jobId)) {
            throw new IllegalStateException("You have already applied for this job.");
        }

        JobApplication application = new JobApplication();
        application.setWorker(worker);
        application.setJobPosting(job);
        application.setStatus(JobApplicationStatus.PENDING);

        jobApplicationRepository.save(application);
    }
}
