package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.JobStatus;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.dto.ai.*;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.JobPostingRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.JobService;
import com.workconnect.api.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationServiceImpl.class);
    private static final int AI_SERVICE_TIMEOUT_SECONDS = 10;

    private final RestTemplate restTemplate;
    private final UserRepository userRepository;
    private final JobPostingRepository jobPostingRepository;
    private final JobService jobService;

    @Value("${ai.service.url:http://127.0.0.1:8000/recommendations/jobs}")
    private String aiServiceUrl;

    @Value("${ai.service.enabled:true}")
    private boolean aiServiceEnabled;

    public RecommendationServiceImpl(RestTemplate restTemplate, UserRepository userRepository, JobPostingRepository jobPostingRepository, JobService jobService) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
        this.jobPostingRepository = jobPostingRepository;
        this.jobService = jobService;
    }

    @Override
    public List<JobListingDto> getJobRecommendations(String workerEmail) {
        logger.info("Getting job recommendations for worker: {}", workerEmail);

        // 1. Get worker profile
        User worker = userRepository.findByEmail(workerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + workerEmail));
        if (!(worker.getProfile() instanceof WorkerProfile)) {
            logger.warn("User {} is not a worker, returning empty recommendations", workerEmail);
            return List.of(); // Not a worker
        }
        WorkerProfile workerProfile = (WorkerProfile) worker.getProfile();

        // 2. Get all open job postings
        List<JobPosting> openJobs = jobPostingRepository.findByStatus(JobStatus.OPEN);
        if (openJobs.isEmpty()) {
            logger.info("No open jobs available for recommendations");
            return List.of();
        }

        // 3. Check if AI service is enabled and available
        if (!aiServiceEnabled) {
            logger.info("AI service is disabled, returning fallback recommendations");
            return List.of();
        }

        try {
            return getAiPoweredRecommendations(workerProfile, openJobs);
        } catch (Exception e) {
            logger.error("AI service failed for user {}: {}. Falling back to basic recommendations",
                        workerEmail, e.getMessage());
            return List.of();
        }
    }

    /**
     * Get AI-powered recommendations using the external AI service
     */
    private List<JobListingDto> getAiPoweredRecommendations(WorkerProfile workerProfile, List<JobPosting> openJobs) {
        logger.info("Attempting AI-powered recommendations for worker profile: {}", workerProfile.getId());

        // Convert the Set<Skill> into a single space-separated string
        String skillsString = "";
        if (workerProfile.getSkills() != null && !workerProfile.getSkills().isEmpty()) {
            skillsString = workerProfile.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.joining(" "));
        }

        if (skillsString.trim().isEmpty()) {
            logger.warn("Worker profile has no skills defined, using fallback recommendations");
            return getFallbackRecommendations(openJobs);
        }

        AiWorkerProfileDto workerPayload = new AiWorkerProfileDto(skillsString);

        // Prepare job postings for AI service
        List<AiJobPostingDto> jobPayloads = openJobs.stream()
                .map(job -> new AiJobPostingDto(job.getId(), job.getRequiredSkills() != null ? job.getRequiredSkills() : ""))
                .collect(Collectors.toList());

        // Prepare the request for the AI service
        AiRecommendationRequestDto request = new AiRecommendationRequestDto(workerPayload, jobPayloads);

        logger.info("Calling AI service at {} with {} jobs and skills: {}", aiServiceUrl, jobPayloads.size(), skillsString);

        try {
            // Call the Python AI service with timeout handling
            AiRecommendationResponseDto response = restTemplate.postForObject(aiServiceUrl, request, AiRecommendationResponseDto.class);

            if (response == null || response.ranked_job_ids() == null || response.ranked_job_ids().isEmpty()) {
                logger.warn("AI service returned empty or null response, using fallback");
                return getFallbackRecommendations(openJobs);
            }

            // Fetch and map the recommended jobs in the correct order
            List<Long> rankedJobIds = response.ranked_job_ids();
            logger.info("AI service returned {} ranked job IDs", rankedJobIds.size());

            List<JobPosting> recommendedJobs = jobPostingRepository.findAllById(rankedJobIds);

            // Create a map for quick lookups to preserve the ranked order from the AI
            Map<Long, JobPosting> jobMap = recommendedJobs.stream()
                    .collect(Collectors.toMap(JobPosting::getId, job -> job));

            // Map the jobs to DTOs in the order provided by the recommendation service
            List<JobListingDto> result = rankedJobIds.stream()
                    .map(jobMap::get)
                    .filter(Objects::nonNull)
                    .map(jobService::mapToJobListingDto)
                    .collect(Collectors.toList());

            logger.info("Successfully generated {} AI-powered recommendations", result.size());
            return result;

        } catch (RestClientException e) {
            logger.error("Failed to call AI service: {}", e.getMessage());
            throw new RuntimeException("AI service communication failed", e);
        }
    }

    /**
     * Fallback recommendation method when AI service is unavailable
     */
    private List<JobListingDto> getFallbackRecommendations(List<JobPosting> openJobs) {
        logger.info("Using fallback recommendation logic with {} open jobs", openJobs.size());

        // Simple fallback: return most recent jobs (up to 10)
        return openJobs.stream()
                .sorted((a, b) -> b.getPostedAt().compareTo(a.getPostedAt()))
                .limit(10)
                .map(jobService::mapToJobListingDto)
                .collect(Collectors.toList());
    }
}
