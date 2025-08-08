package com.workconnect.api.dto;

import com.workconnect.api.entity.WorkerProfile;
import lombok.Data;
import com.workconnect.api.entity.Skill;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Data
public class WorkerProfileDto {
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String location;
    private String bio;
    private String experience;
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
