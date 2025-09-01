package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.dto.CreateReviewDto;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.*;
import com.workconnect.api.service.BadgeService;
import com.workconnect.api.service.ReviewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final JobApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final BadgeService badgeService;

    public ReviewServiceImpl(ReviewRepository reviewRepository, JobApplicationRepository applicationRepository,
                             UserRepository userRepository, BadgeService badgeService) {
        this.reviewRepository = reviewRepository;
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.badgeService = badgeService;
    }

    @Transactional
    @Override
    public void createReview(String reviewerEmail, CreateReviewDto createReviewDto) {
        User reviewer = userRepository.findByEmail(reviewerEmail).orElseThrow();
        JobApplication application = applicationRepository.findById(createReviewDto.getApplicationId()).orElseThrow();

        if (application.getStatus() != JobApplicationStatus.COMPLETED) {
            throw new IllegalStateException("You can only review completed jobs.");
        }

        User worker = application.getWorker();
        User employer = application.getJobPosting().getEmployer();

        User reviewee;
        if (reviewer.equals(worker)) {
            reviewee = employer;
        } else if (reviewer.equals(employer)) {
            reviewee = worker;
        } else {
            throw new org.springframework.security.access.AccessDeniedException("You were not part of this job.");
        }

        Review review = new Review();
        review.setRating(createReviewDto.getRating());
        review.setComment(createReviewDto.getComment());
        review.setJobApplication(application);

        review.setReviewer(reviewer);
        review.setReviewee(reviewee);

        reviewRepository.save(review);

        badgeService.checkAndAwardBadges(reviewee);
    }
}
