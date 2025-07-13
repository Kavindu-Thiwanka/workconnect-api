package com.kt.workconnect.controller;

import com.kt.workconnect.dto.ReviewDTO;
import com.kt.workconnect.entity.Review;
import com.kt.workconnect.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    @Autowired
    private ReviewService reviewService;

    @PostMapping
    public ResponseEntity<?> createReview(@RequestBody ReviewDTO reviewDTO, @AuthenticationPrincipal Jwt jwt) {
        try {
            String reviewerEmail = jwt.getClaimAsString("email");
            Review createdReview = reviewService.createReview(reviewDTO, reviewerEmail);
            return ResponseEntity.ok(createdReview);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Review>> getReviewsForUser(@PathVariable UUID userId) {
        return ResponseEntity.ok(reviewService.getReviewsForUser(userId));
    }
}
