package com.workconnect.api.service.impl;

import com.workconnect.api.constants.Enum.JobApplicationStatus;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.BadgeRepository;
import com.workconnect.api.repository.JobApplicationRepository;
import com.workconnect.api.repository.UserBadgeRepository;
import com.workconnect.api.service.BadgeService;
import org.springframework.stereotype.Service;

@Service
public class BadgeServiceImpl implements BadgeService {

    private final UserBadgeRepository userBadgeRepository;
    private final BadgeRepository badgeRepository;
    private final JobApplicationRepository applicationRepository;

    public BadgeServiceImpl(UserBadgeRepository userBadgeRepository, BadgeRepository badgeRepository, JobApplicationRepository applicationRepository) {
        this.userBadgeRepository = userBadgeRepository;
        this.badgeRepository = badgeRepository;
        this.applicationRepository = applicationRepository;
    }

    @Override
    public void checkAndAwardJobCompletionBadges(User worker) {
        long completedJobs = applicationRepository.countByWorkerAndStatus(worker, JobApplicationStatus.COMPLETED);

        if (completedJobs >= 5) {
            awardBadge(worker, "FIVE_JOBS_COMPLETED");
        } else if (completedJobs >= 1) {
            awardBadge(worker, "FIRST_JOB_COMPLETED");
        }
    }

    private void awardBadge(User user, String badgeName) {
        Badge badge = badgeRepository.findByName(badgeName);
        if (badge != null && !userBadgeRepository.existsByUserAndBadge(user, badge)) {
            UserBadge userBadge = new UserBadge();
            userBadge.setUser(user);
            userBadge.setBadge(badge);
            userBadgeRepository.save(userBadge);
        }
    }
}
