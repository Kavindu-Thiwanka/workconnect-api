package com.kt.workconnect.service.impl;

import com.kt.workconnect.constant.Enum.UserRole;
import com.kt.workconnect.dto.JobDTO;
import com.kt.workconnect.entity.Job;
import com.kt.workconnect.entity.User;
import com.kt.workconnect.repository.JobRepository;
import com.kt.workconnect.repository.UserRepository;
import com.kt.workconnect.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Job createJob(JobDTO jobDTO, String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Employer not found"));

        if (employer.getUserRole() != UserRole.EMPLOYER) {
            throw new IllegalStateException("Only employers can post jobs.");
        }

        Job newJob = new Job();
        newJob.setTitle(jobDTO.getTitle());
        newJob.setDescription(jobDTO.getDescription());
        newJob.setLocation(jobDTO.getLocation());
        newJob.setSalary(jobDTO.getSalary());
        newJob.setRequiredSkills(jobDTO.getRequiredSkills());
        newJob.setPostedBy(employer);

        return jobRepository.save(newJob);
    }

    @Override
    public Optional<Job> getJobById(UUID jobId) {
        return jobRepository.findById(jobId);
    }

    @Override
    public List<Job> getAllJobs() {
        return jobRepository.findAll();
    }
}
