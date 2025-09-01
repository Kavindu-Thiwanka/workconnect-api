package com.workconnect.api.dto.ai;

import java.util.List;

public record AiRecommendationResponseDto(List<Long> ranked_job_ids) {}
