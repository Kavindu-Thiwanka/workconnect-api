package com.workconnect.api.controller;

import com.workconnect.api.dto.BadgeDto;
import com.workconnect.api.dto.ReviewDto;
import com.workconnect.api.service.PublicProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/profiles")
public class PublicProfileController {

    private final PublicProfileService publicProfileService;

    public PublicProfileController(PublicProfileService publicProfileService) {
        this.publicProfileService = publicProfileService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getPublicProfileData(@PathVariable Long userId) {
        Map<String, Object> profileData = publicProfileService.getPublicProfileData(userId);
        return ResponseEntity.ok(profileData);
    }
}
