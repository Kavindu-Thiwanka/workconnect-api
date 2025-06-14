package com.kt.workconnect.service.impl;

import com.kt.workconnect.constant.Enum.UserRole;
import com.kt.workconnect.entity.Job;
import com.kt.workconnect.entity.JobApplication;
import com.kt.workconnect.entity.User;
import com.kt.workconnect.repository.JobApplicationRepository;
import com.kt.workconnect.repository.JobRepository;
import com.kt.workconnect.repository.UserRepository;
import com.kt.workconnect.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private JobApplicationRepository applicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JobRepository jobRepository;

    @Override
    public JobApplication applyForJob(UUID jobId, String applicantEmail) {
        User applicant = userRepository.findByEmail(applicantEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + applicantEmail));

        if (applicant.getUserRole() != UserRole.WORKER) {
            throw new IllegalStateException("Only users with the WORKER role can apply for jobs.");
        }

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + jobId));

        JobApplication newApplication = new JobApplication();
        newApplication.setApplicant(applicant);
        newApplication.setJob(job);

        return applicationRepository.save(newApplication);
    }
}
