package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.BadgeCriteriaType;
import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.BadgeRepository;
import com.workconnect.api.repository.JobApplicationRepository;
import com.workconnect.api.repository.ReviewRepository;
import com.workconnect.api.repository.UserBadgeRepository;
import com.workconnect.api.service.BadgeService;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class BadgeServiceImpl implements BadgeService {

    private final BadgeRepository badgeRepository;
    private final UserBadgeRepository userBadgeRepository;
    private final JobApplicationRepository applicationRepository;
    private final ReviewRepository reviewRepository;

    public BadgeServiceImpl(BadgeRepository badgeRepository, UserBadgeRepository userBadgeRepository, JobApplicationRepository applicationRepository, ReviewRepository reviewRepository) {
        this.badgeRepository = badgeRepository;
        this.userBadgeRepository = userBadgeRepository;
        this.applicationRepository = applicationRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public void checkAndAwardBadges(User user) {
        List<Badge> allBadges = badgeRepository.findAll();

        for (Badge badge : allBadges) {
            if (userBadgeRepository.existsByUserAndBadge(user, badge)) {
                continue;
            }

            boolean eligible = false;
            if (badge.getCriteriaType() == BadgeCriteriaType.JOB_COMPLETION_COUNT) {
                long completedJobs = applicationRepository.countByWorkerAndStatus(user, JobApplicationStatus.COMPLETED);
                if (completedJobs >= badge.getCriteriaValue()) {
                    eligible = true;
                }
            } else if (badge.getCriteriaType() == BadgeCriteriaType.AVERAGE_RATING) {
                List<Review> reviews = reviewRepository.findByReviewee(user);
                if (!reviews.isEmpty()) {
                    double averageRating = reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
                    if (averageRating >= badge.getCriteriaValue()) {
                        eligible = true;
                    }
                }
            }

            if (eligible) {
                awardBadge(user, badge);
            }
        }
    }

    private void awardBadge(User user, Badge badge) {
        UserBadge userBadge = new UserBadge();
        userBadge.setUser(user);
        userBadge.setBadge(badge);
        userBadgeRepository.save(userBadge);
    }
}
