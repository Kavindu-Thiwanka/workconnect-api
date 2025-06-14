package com.kt.workconnect.service;

import com.kt.workconnect.dto.ProfileDTO;
import com.kt.workconnect.entity.User;

public interface ProfileService {
    Object createOrUpdateProfile(ProfileDTO profileDTO, User user);
}
