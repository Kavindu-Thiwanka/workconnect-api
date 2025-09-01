package com.workconnect.api.service;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.dto.CreateJobRequestDto;
import com.workconnect.api.dto.UpdateJobRequestDto;
import com.workconnect.api.dto.JobApplicationDto;
import com.workconnect.api.dto.JobDetailDto;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.dto.ApplicationStatusResponse;
import com.workconnect.api.entity.JobApplication;
import com.workconnect.api.entity.JobImage;
import com.workconnect.api.entity.JobPosting;

import java.util.List;

public interface JobService {

    JobPosting createJob(String employerEmail, CreateJobRequestDto jobDto);

    JobPosting updateJob(String employerEmail, Long jobId, UpdateJobRequestDto jobDto);

    void deleteJob(String employerEmail, Long jobId);

    JobPosting updateJobStatus(String employerEmail, Long jobId, com.workconnect.api.constants.Enum.JobStatus status);

    List<JobListingDto> getAllOpenJobs();

    List<JobListingDto> searchOpenJobs(String keyword);

    JobDetailDto getJobById(Long jobId);

    void applyForJob(String workerEmail, Long jobId);

    ApplicationStatusResponse checkApplicationStatus(String workerEmail, Long jobId);

    List<JobApplicationDto> getApplicationsForJob(String employerEmail, Long jobId);

    JobApplication updateApplicationStatus(String employerEmail, Long applicationId, JobApplicationStatus status);

    JobListingDto mapToJobListingDto(JobPosting job);

    JobImage addImageToJob(String employerEmail, Long jobId, String imageUrl);

    List<JobListingDto> getJobsByEmployer(String employerEmail);
}
