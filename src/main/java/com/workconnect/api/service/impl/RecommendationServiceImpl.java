package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.dto.ai.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.JobPostingRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.JobService;
import com.workconnect.api.service.RecommendationService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobService jobService;

    @Value("${ai.service.url:http://127.0.0.1:8000/recommendations/jobs}")
    private String aiServiceUrl;

    public RecommendationServiceImpl(RestTemplate restTemplate, UserRepository userRepository, JobPostingRepository jobPostingRepository, JobService jobService) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobService = jobService;
    }

    @Override
    public List<JobListingDto> getJobRecommendations(String workerEmail) {
        // 1. Get worker profile
        User worker = userRepository.findByEmail(workerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + workerEmail));
        if (!(worker.getProfile() instanceof WorkerProfile)) {
            return List.of(); // Not a worker
        }
        WorkerProfile workerProfile = (WorkerProfile) worker.getProfile();

        // Convert the Set<Skill> into a single space-separated string
        String skillsString = "";
        if (workerProfile.getSkills() != null && !workerProfile.getSkills().isEmpty()) {
            skillsString = workerProfile.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.joining(" "));
        }

        AiWorkerProfileDto workerPayload = new AiWorkerProfileDto(skillsString);

        // Get all open job postings
        List<JobPosting> openJobs = jobPostingRepository.findByStatus(JobStatus.OPEN);
        List<AiJobPostingDto> jobPayloads = openJobs.stream()
                .map(job -> new AiJobPostingDto(job.getId(), job.getRequiredSkills() != null ? job.getRequiredSkills() : ""))
                .collect(Collectors.toList());

        // Prepare the request for the AI service
        AiRecommendationRequestDto request = new AiRecommendationRequestDto(workerPayload, jobPayloads);

        // Call the Python AI service
        AiRecommendationResponseDto response = restTemplate.postForObject(aiServiceUrl, request, AiRecommendationResponseDto.class);

        if (response == null || response.ranked_job_ids() == null || response.ranked_job_ids().isEmpty()) {
            return List.of();
        }

        // Fetch and map the recommended jobs in the correct order
        List<Long> rankedJobIds = response.ranked_job_ids();
        List<JobPosting> recommendedJobs = jobPostingRepository.findAllById(rankedJobIds);

        // Create a map for quick lookups to preserve the ranked order from the AI
        Map<Long, JobPosting> jobMap = recommendedJobs.stream()
                .collect(Collectors.toMap(JobPosting::getId, job -> job));

        // Map the jobs to DTOs in the order provided by the recommendation service
        return rankedJobIds.stream()
                .map(jobMap::get)
                .filter(Objects::nonNull)
                .map(jobService::mapToJobListingDto)
                .collect(Collectors.toList());
    }
}
