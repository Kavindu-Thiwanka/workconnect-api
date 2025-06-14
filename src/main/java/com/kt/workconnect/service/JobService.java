package com.kt.workconnect.service;

import com.kt.workconnect.dto.JobDTO;
import com.kt.workconnect.entity.Job;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobService {
    Job createJob(JobDTO jobDTO, String employerEmail);
    Optional<Job> getJobById(UUID jobId);
    List<Job> getAllJobs();
    List<Job> getJobsByEmployer(String employerEmail);
}
