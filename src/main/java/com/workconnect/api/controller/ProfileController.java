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
import java.util.Map;

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
    public ResponseEntity<Object> uploadProfilePicture(@RequestParam("file") MultipartFile file, Principal principal) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("error", "Please select a file to upload"));
            }

            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest().body(Map.of("error", "Only image files are allowed"));
            }

            // Validate file size (5MB limit)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest().body(Map.of("error", "File size must be less than 5MB"));
            }

            String imageUrl = fileUploadService.uploadFile(file);
            profileService.updateProfilePicture(principal.getName(), imageUrl);
            Map<String, String> response = Map.of("imageUrl", imageUrl);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to upload image: " + e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "An unexpected error occurred: " + e.getMessage()));
        }
    }
}
