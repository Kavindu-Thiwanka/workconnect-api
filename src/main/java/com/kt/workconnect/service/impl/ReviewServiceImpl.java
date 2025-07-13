package com.kt.workconnect.service.impl;

import com.kt.workconnect.dto.ReviewDTO;
import com.kt.workconnect.entity.Job;
import com.kt.workconnect.entity.Review;
import com.kt.workconnect.entity.User;
import com.kt.workconnect.repository.JobRepository;
import com.kt.workconnect.repository.ReviewRepository;
import com.kt.workconnect.repository.UserRepository;
import com.kt.workconnect.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JobRepository jobRepository;

    @Override
    public Review createReview(ReviewDTO reviewDTO, String reviewerEmail) {
        User reviewer = userRepository.findByEmail(reviewerEmail)
                .orElseThrow(() -> new UsernameNotFoundException("Reviewer not found"));

        User reviewee = userRepository.findById(reviewDTO.getRevieweeId())
                .orElseThrow(() -> new IllegalArgumentException("User being reviewed not found"));

        Job job = jobRepository.findById(reviewDTO.getJobId())
                .orElseThrow(() -> new IllegalArgumentException("Job not found"));

        // Basic validation: Ensure the reviewer is either the employer or the applicant
        boolean isEmployer = job.getPostedBy().getId().equals(reviewer.getId());
        boolean isWorker = job.getJobApplications().stream()
                .anyMatch(app -> app.getApplicant().getId().equals(reviewer.getId()));

        if (!isEmployer && !isWorker) {
            throw new IllegalStateException("You are not authorized to review this job.");
        }

        Review newReview = new Review();
        newReview.setJob(job);
        newReview.setReviewer(reviewer);
        newReview.setReviewee(reviewee);
        newReview.setRating(reviewDTO.getRating());
        newReview.setComment(reviewDTO.getComment());

        return reviewRepository.save(newReview);
    }

    @Override
    public List<Review> getReviewsForUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return reviewRepository.findByReviewee(user);
    }
}
