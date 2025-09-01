package com.workconnect.api.dto;

import com.workconnect.api.entity.Review;
import lombok.Data;

@Data
public class ReviewDto {
    private int rating;
    private String comment;
    private String reviewerName;

    public static ReviewDto fromEntity(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setRating(review.getRating());
        dto.setComment(review.getComment());

        if (review.getReviewer().getProfile() instanceof com.workconnect.api.entity.WorkerProfile) {
            dto.setReviewerName(((com.workconnect.api.entity.WorkerProfile) review.getReviewer().getProfile()).getFirstName());
        } else if (review.getReviewer().getProfile() instanceof com.workconnect.api.entity.EmployerProfile) {
            dto.setReviewerName(((com.workconnect.api.entity.EmployerProfile) review.getReviewer().getProfile()).getCompanyName());
        }
        return dto;
    }
}
