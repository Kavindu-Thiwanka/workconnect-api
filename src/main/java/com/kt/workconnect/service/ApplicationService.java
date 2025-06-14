package com.kt.workconnect.service;

import com.kt.workconnect.entity.JobApplication;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.UUID;

public interface ApplicationService {

    JobApplication applyForJob(UUID jobId, String applicantEmail);

    List<JobApplication> getApplicationsForJob(UUID jobId, String employerEmail) throws AccessDeniedException;
}
