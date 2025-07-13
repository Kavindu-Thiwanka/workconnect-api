package com.workconnect.api.service;

import com.workconnect.api.entity.User;

public interface BadgeService {

    void checkAndAwardJobCompletionBadges(User worker);

}
