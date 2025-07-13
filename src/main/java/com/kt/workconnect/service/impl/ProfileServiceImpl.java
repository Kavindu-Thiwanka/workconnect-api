package com.kt.workconnect.service.impl;

import com.kt.workconnect.constant.Enum.UserRole;
import com.kt.workconnect.dto.ProfileDTO;
import com.kt.workconnect.entity.EmployerProfile;
import com.kt.workconnect.entity.User;
import com.kt.workconnect.entity.WorkerProfile;
import com.kt.workconnect.repository.EmployerProfileRepository;
import com.kt.workconnect.repository.WorkerProfileRepository;
import com.kt.workconnect.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private WorkerProfileRepository workerProfileRepository;

    @Autowired
    private EmployerProfileRepository employerProfileRepository;

    @Override
    public Object createOrUpdateProfile(ProfileDTO profileDTO, User user) {
        if (user.getUserRole() == UserRole.WORKER) {
            WorkerProfile profile;
            Optional<WorkerProfile> existingProfileOpt = workerProfileRepository.findByUser(user);
            profile = existingProfileOpt.orElseGet(WorkerProfile::new);
            profile.setUser(user);
            profile.setHeadline(profileDTO.getHeadline());
            profile.setSkills(profileDTO.getSkills());
            profile.setExperience(profileDTO.getExperience());
            profile.setAvailability(profileDTO.getAvailability());
            return workerProfileRepository.save(profile);
        } else if (user.getUserRole() == UserRole.EMPLOYER) {
            EmployerProfile profile;
            Optional<EmployerProfile> existingProfileOpt = employerProfileRepository.findByUser(user);
            profile = existingProfileOpt.orElseGet(EmployerProfile::new);
            profile.setUser(user);
            profile.setCompanyName(profileDTO.getCompanyName());
            profile.setIndustry(profileDTO.getIndustry());
            profile.setDescription(profileDTO.getDescription());
            return employerProfileRepository.save(profile);
        }
        throw new IllegalStateException("User role not supported for profile creation.");
    }
}
