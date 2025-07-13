package com.kt.workconnect.service;

import com.kt.workconnect.dto.ReviewDTO;
import com.kt.workconnect.entity.Review;

import java.util.List;
import java.util.UUID;

public interface ReviewService {
    Review createReview(ReviewDTO reviewDTO, String reviewerEmail);

    List<Review> getReviewsForUser(UUID userId);
}
