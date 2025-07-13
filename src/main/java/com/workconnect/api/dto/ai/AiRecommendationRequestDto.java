package com.workconnect.api.dto.ai;

import java.util.List;

public record AiRecommendationRequestDto(AiWorkerProfileDto worker_profile, List<AiJobPostingDto> job_postings) {}
