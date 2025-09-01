package com.workconnect.api.service;

import java.util.Map;

public interface PublicProfileService {
    Map<String, Object> getPublicProfileData(Long userId);
}
