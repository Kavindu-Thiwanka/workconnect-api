package com.workconnect.api.controller;

import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.dto.dashboard.JobRecommendationsDto;
import com.workconnect.api.service.RecommendationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

/**
 * REST Controller for job recommendations
 */
@RestController
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private static final Logger logger = LoggerFactory.getLogger(RecommendationController.class);

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    /**
     * Get AI-powered job recommendations for the authenticated worker
     */
    @GetMapping("/jobs")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<JobRecommendationsDto> getJobRecommendations(
            Principal principal,
            @RequestParam(defaultValue = "10") int limit) {
        
        String userEmail = principal.getName();
        logger.info("Getting job recommendations for user: {} with limit: {}", userEmail, limit);
        
        try {
            List<JobListingDto> recommendations = recommendationService.getJobRecommendations(userEmail);
            
            // Limit the results if needed
            if (recommendations.size() > limit) {
                recommendations = recommendations.subList(0, limit);
            }
            
            JobRecommendationsDto response = JobRecommendationsDto.builder()
                    .recommendations(recommendations)
                    .totalCount(recommendations.size())
                    .recommendationReason(recommendations.isEmpty() ? 
                        "No recommendations available at this time" : 
                        "AI-powered recommendations based on your skills and experience")
                    .build();
            
            logger.info("Successfully returned {} recommendations for user: {}", 
                       recommendations.size(), userEmail);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error getting recommendations for user {}: {}", userEmail, e.getMessage());
            
            // Return empty recommendations with error message
            JobRecommendationsDto errorResponse = JobRecommendationsDto.builder()
                    .recommendations(List.of())
                    .totalCount(0)
                    .recommendationReason("Unable to generate recommendations at this time. Please try again later.")
                    .build();
            
            return ResponseEntity.ok(errorResponse);
        }
    }

    /**
     * Health check endpoint for the recommendation service
     */
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("Recommendation service is running");
    }
}
