package com.workconnect.api.controller;

import com.workconnect.api.dto.EmployerProfileDto;
import com.workconnect.api.dto.JobListingDto;
import com.workconnect.api.dto.WorkerProfileDto;
import com.workconnect.api.service.FileUploadService;
import com.workconnect.api.service.ProfileService;
import com.workconnect.api.service.RecommendationService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/profiles")
public class ProfileController {

    private final ProfileService profileService;
    private final RecommendationService recommendationService;
    private final FileUploadService fileUploadService;

    public ProfileController(ProfileService profileService, RecommendationService recommendationService, FileUploadService fileUploadService) {
        this.profileService = profileService;
        this.recommendationService = recommendationService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("/me")
    public ResponseEntity<Object> getCurrentUserProfile(Principal principal) {
        Object profileDto = profileService.getProfile(principal.getName());
        return ResponseEntity.ok(profileDto);
    }

    @PutMapping("/me/worker")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<WorkerProfileDto> updateWorkerProfile(Principal principal, @Valid @RequestBody WorkerProfileDto profileDto) {
        WorkerProfileDto updatedProfile = profileService.updateWorkerProfile(principal.getName(), profileDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @PutMapping("/me/employer")
    @PreAuthorize("hasRole('EMPLOYER')")
    public ResponseEntity<EmployerProfileDto> updateEmployerProfile(Principal principal, @Valid @RequestBody EmployerProfileDto profileDto) {
        EmployerProfileDto updatedProfile = profileService.updateEmployerProfile(principal.getName(), profileDto);
        return ResponseEntity.ok(updatedProfile);
    }

    @GetMapping("/me/recommendations")
    @PreAuthorize("hasRole('WORKER')")
    public ResponseEntity<List<JobListingDto>> getRecommendations(Principal principal) {
        List<JobListingDto> recommendations = recommendationService.getJobRecommendations(principal.getName());
        return ResponseEntity.ok(recommendations);
    }

    @PostMapping("/me/picture")
    public ResponseEntity<String> uploadProfilePicture(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            String imageUrl = fileUploadService.uploadFile(file);
            profileService.updateProfilePicture(principal.getName(), imageUrl);
            return ResponseEntity.ok("Profile picture updated successfully: " + imageUrl);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload image.");
        }
    }
}
