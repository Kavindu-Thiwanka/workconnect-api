package com.workconnect.api.service.impl;

import com.workconnect.api.dto.EmployerProfileDto;
import com.workconnect.api.dto.WorkerProfileDto;
import com.workconnect.api.entity.*;
import com.workconnect.api.repository.SkillRepository;
import com.workconnect.api.repository.UserRepository;
import com.workconnect.api.service.ProfileService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Service
public class ProfileServiceImpl implements ProfileService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    public ProfileServiceImpl(UserRepository userRepository, SkillRepository skillRepository) {
        this.userRepository = userRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public Object getProfile(String email) {
        User user = findUserByEmail(email);
        Profile profile = user.getProfile();

        if (profile instanceof WorkerProfile) {
            return mapToWorkerDto((WorkerProfile) profile);
        } else if (profile instanceof EmployerProfile) {
            return mapToEmployerDto((EmployerProfile) profile);
        }
        return null;
    }

    @Transactional
    @Override
    public WorkerProfileDto updateWorkerProfile(String email, WorkerProfileDto workerProfileDto) {
        User user = findUserByEmail(email);
        if (!(user.getProfile() instanceof WorkerProfile)) {
            throw new IllegalStateException("User is not a worker.");
        }
        WorkerProfile workerProfile = (WorkerProfile) user.getProfile();

        workerProfile.setFirstName(workerProfileDto.getFirstName());
        workerProfile.setLastName(workerProfileDto.getLastName());
        workerProfile.setLocation(workerProfileDto.getLocation());
        workerProfile.setExperience(workerProfileDto.getExperience());
        workerProfile.setAvailability(workerProfileDto.getAvailability());

        if (workerProfileDto.getSkills() != null) {
            Set<Skill> skillEntities = new HashSet<>();
            for (String skillName : workerProfileDto.getSkills()) {
                Skill skill = skillRepository.findByNameIgnoreCase(skillName)
                        .orElseGet(() -> skillRepository.save(new Skill(skillName)));
                skillEntities.add(skill);
            }
            workerProfile.setSkills(skillEntities);
        }

        userRepository.save(user);
        return mapToWorkerDto(workerProfile);
    }

    @Transactional
    @Override
    public EmployerProfileDto updateEmployerProfile(String email, EmployerProfileDto employerProfileDto) {
        User user = findUserByEmail(email);
        if (!(user.getProfile() instanceof EmployerProfile)) {
            throw new IllegalStateException("User is not an employer.");
        }
        EmployerProfile employerProfile = (EmployerProfile) user.getProfile();

        employerProfile.setCompanyName(employerProfileDto.getCompanyName());
        employerProfile.setCompanyDescription(employerProfileDto.getCompanyDescription());
        employerProfile.setLocation(employerProfileDto.getLocation());

        userRepository.save(user);
        return mapToEmployerDto(employerProfile);
    }

    private User findUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private WorkerProfileDto mapToWorkerDto(WorkerProfile profile) {
        WorkerProfileDto dto = new WorkerProfileDto();
        dto.setFirstName(profile.getFirstName());
        dto.setLastName(profile.getLastName());
        dto.setLocation(profile.getLocation());
        Set<String> skills = new HashSet<>();
        for (Skill skill : profile.getSkills()) {
            skills.add(skill.getName());
        }
        dto.setSkills(skills);
        dto.setExperience(profile.getExperience());
        dto.setAvailability(profile.getAvailability());
        return dto;
    }

    private EmployerProfileDto mapToEmployerDto(EmployerProfile profile) {
        EmployerProfileDto dto = new EmployerProfileDto();
        dto.setCompanyName(profile.getCompanyName());
        dto.setCompanyDescription(profile.getCompanyDescription());
        dto.setLocation(profile.getLocation());
        return dto;
    }

    @Transactional
    @Override
    public void updateProfilePicture(String email, String imageUrl) {
        User user = findUserByEmail(email);
        Profile profile = user.getProfile();

        if (profile instanceof WorkerProfile) {
            ((WorkerProfile) profile).setProfileImageUrl(imageUrl);
        } else if (profile instanceof EmployerProfile) {
            ((EmployerProfile) profile).setCompanyLogoUrl(imageUrl);
        }

        userRepository.save(user);
    }
}
