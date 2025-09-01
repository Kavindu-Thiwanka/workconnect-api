package com.workconnect.api.controller;

import com.workconnect.api.dto.CreateReviewDto;
import com.workconnect.api.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    @PreAuthorize("hasRole('WORKER') or hasRole('EMPLOYER')")
    public ResponseEntity<String> submitReview(@Valid @RequestBody CreateReviewDto createReviewDto, Principal principal) {
        reviewService.createReview(principal.getName(), createReviewDto);
        return ResponseEntity.status(HttpStatus.CREATED).body("Review submitted successfully.");
    }
}
