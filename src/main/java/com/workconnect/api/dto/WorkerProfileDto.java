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
    private String location;
    private String experience;
    private String availability;
    private Set<String> skills = new HashSet<>();
    private String profileImageUrl;

    public static WorkerProfileDto fromEntity(WorkerProfile profile) {
        WorkerProfileDto dto = new WorkerProfileDto();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setLocation(profile.getLocation());
        dto.setExperience(profile.getExperience());
        dto.setAvailability(profile.getAvailability());
        dto.setProfileImageUrl(profile.getProfileImageUrl());

        if (profile.getSkills() != null) {
            dto.setSkills(profile.getSkills().stream()
                    .map(Skill::getName)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }
}
