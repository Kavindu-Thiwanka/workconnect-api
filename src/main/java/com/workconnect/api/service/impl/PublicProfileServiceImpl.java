package com.workconnect.api.service.impl;

import com.workconnect.api.dto.BadgeDto;
import com.workconnect.api.dto.EmployerProfileDto;
import com.workconnect.api.dto.ReviewDto;
import com.workconnect.api.dto.WorkerProfileDto;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.ReviewRepository;
import com.workconnect.api.repository.UserBadgeRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.PublicProfileService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class PublicProfileServiceImpl implements PublicProfileService {

    private final UserRepository userRepository;
    private final ReviewRepository reviewRepository;
    private final UserBadgeRepository userBadgeRepository;

    public PublicProfileServiceImpl(UserRepository userRepository, ReviewRepository reviewRepository, UserBadgeRepository userBadgeRepository) {
        this.userRepository = userRepository;
        this.reviewRepository = reviewRepository;
        this.userBadgeRepository = userBadgeRepository;
    }

    @Override
    public Map<String, Object> getPublicProfileData(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Map<String, Object> response = new HashMap<>();

        if (user.getProfile() instanceof WorkerProfile) {
            response.put("profile", WorkerProfileDto.fromEntity((WorkerProfile) user.getProfile()));
        } else if (user.getProfile() instanceof EmployerProfile) {
            response.put("profile", EmployerProfileDto.fromEntity((EmployerProfile) user.getProfile()));
        }

        List<Review> reviews = reviewRepository.findByReviewee(user);
        List<ReviewDto> reviewDtos = reviews.stream()
                .map(ReviewDto::fromEntity)
                .collect(Collectors.toList());
        response.put("reviews", reviewDtos);

        List<UserBadge> userBadges = userBadgeRepository.findByUser(user);
        List<BadgeDto> badgeDtos = userBadges.stream()
                .map(userBadge -> BadgeDto.fromEntity(userBadge.getBadge()))
                .collect(Collectors.toList());
        response.put("badges", badgeDtos);

        return response;
    }
}
