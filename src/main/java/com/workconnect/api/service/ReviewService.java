package com.workconnect.api.service;

import com.workconnect.api.dto.CreateReviewDto;

public interface ReviewService {

    void createReview(String reviewerEmail, CreateReviewDto createReviewDto);

}
