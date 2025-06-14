package com.kt.workconnect.service.impl;

import com.kt.workconnect.constant.Enum.UserRole;
import com.kt.workconnect.dto.JobDTO;
import com.kt.workconnect.dto.ai.AiJobDTO;
import com.kt.workconnect.dto.ai.AiRequestDTO;
import com.kt.workconnect.dto.ai.AiResponseDTO;
import com.kt.workconnect.dto.ai.AiWorkerProfileDTO;
import com.kt.workconnect.entity.Job;
import com.kt.workconnect.entity.User;
import com.kt.workconnect.entity.WorkerProfile;
import com.kt.workconnect.repository.JobRepository;
import com.kt.workconnect.repository.UserRepository;
import com.kt.workconnect.repository.WorkerProfileRepository;
import com.kt.workconnect.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Autowired
    private RestTemplate restTemplate;

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

    @Override
    public List<Job> getJobsByEmployer(String employerEmail) {
        User employer = userRepository.findByEmail(employerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Employer not found"));

        return jobRepository.findByPostedBy(employer);
    }

    @Override
    public List<Job> getRecommendedJobs(String workerEmail) {
        // 1. Fetch the worker's profile
        User worker = userRepository.findByEmail(workerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Worker not found"));
        WorkerProfile workerProfile = workerProfileRepository.findByUser(worker)
                .orElseThrow(() -> new IllegalStateException("Worker profile not found. Please create a profile first."));

        // 2. Fetch all available jobs
        List<Job> allJobs = jobRepository.findAll();
        if (allJobs.isEmpty()) {
            return Collections.emptyList();
        }

        // 3. Prepare the request for the AI service
        AiRequestDTO aiRequest = new AiRequestDTO();

        AiWorkerProfileDTO workerDto = new AiWorkerProfileDTO();
        workerDto.setSkills(workerProfile.getSkills());
        workerDto.setExperience(workerProfile.getExperience());
        aiRequest.setWorker_profile(workerDto);

        List<AiJobDTO> jobDtos = allJobs.stream().map(job -> {
            AiJobDTO dto = new AiJobDTO();
            dto.setId(job.getId());
            dto.setTitle(job.getTitle());
            dto.setDescription(job.getDescription());
            dto.setRequiredSkills(job.getRequiredSkills());
            return dto;
        }).collect(Collectors.toList());
        aiRequest.setJobs(jobDtos);

        // 4. Call the Python AI service
        String aiServiceUrl = "http://localhost:5001/match";
        AiResponseDTO aiResponse = restTemplate.postForObject(aiServiceUrl, aiRequest, AiResponseDTO.class);

        if (aiResponse == null || aiResponse.getRecommended_job_ids() == null) {
            return Collections.emptyList();
        }

        // 5. Fetch the recommended jobs from the database based on the IDs
        List<UUID> recommendedJobIds = aiResponse.getRecommended_job_ids();
        return jobRepository.findAllById(recommendedJobIds);
    }
}
