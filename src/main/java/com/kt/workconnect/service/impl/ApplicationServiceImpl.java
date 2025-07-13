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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public List<JobApplication> getApplicationsForJob(UUID jobId, String employerEmail) throws AccessDeniedException {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + employerEmail));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new IllegalArgumentException("Job not found with ID: " + jobId));

        // Security Check: Ensure the user requesting the applications is the one who posted the job
        if (!job.getPostedBy().getId().equals(employer.getId())) {
            throw new AccessDeniedException("You are not authorized to view applications for this job.");
        }

        return applicationRepository.findByJob(job);
    }

    @Override
    public List<JobApplication> getAllJobApplications(String workerEmail) {

        User worker = userRepository.findByEmail(workerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + workerEmail));

        return applicationRepository.findAllByApplicant(worker);
    }
}
