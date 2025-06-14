package com.kt.workconnect.service;

import com.kt.workconnect.entity.JobApplication;

import java.util.UUID;

public interface ApplicationService {

    JobApplication applyForJob(UUID jobId, String applicantEmail);
}
