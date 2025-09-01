package com.workconnect.api.service;

import com.workconnect.api.dto.JobListingDto;
import java.util.List;

public interface RecommendationService {

    List<JobListingDto> getJobRecommendations(String workerEmail);
}
