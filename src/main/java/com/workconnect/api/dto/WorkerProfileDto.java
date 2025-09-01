package com.workconnect.api.dto;

import com.workconnect.api.entity.WorkerProfile;
import lombok.Data;
import com.workconnect.api.entity.Skill;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class WorkerProfileDto {
    @Size(max = 50, message = "First name must not exceed 50 characters")
    private String firstName;

    @Size(max = 50, message = "Last name must not exceed 50 characters")
    private String lastName;

    @Size(max = 20, message = "Phone number must not exceed 20 characters")
    private String phoneNumber;

    @Size(max = 100, message = "Location must not exceed 100 characters")
    private String location;

    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;

    @Size(max = 1000, message = "Experience must not exceed 1000 characters")
    private String experience;

    @Size(max = 1000, message = "Education must not exceed 1000 characters")
    private String education;

    private String availability;
    private Set<String> skills = new HashSet<>();
    private String profileImageUrl;
    private String resumeUrl;

    public static WorkerProfileDto fromEntity(WorkerProfile profile) {
        WorkerProfileDto dto = new WorkerProfileDto();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setPhoneNumber(profile.getPhoneNumber());
        dto.setLocation(profile.getLocation());
        dto.setBio(profile.getBio());
        dto.setExperience(profile.getExperience());
        dto.setEducation(profile.getEducation());
        dto.setAvailability(profile.getAvailability());
        dto.setProfileImageUrl(profile.getProfileImageUrl());
        dto.setResumeUrl(profile.getResumeUrl());

        if (profile.getSkills() != null) {
            dto.setSkills(profile.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }
}
